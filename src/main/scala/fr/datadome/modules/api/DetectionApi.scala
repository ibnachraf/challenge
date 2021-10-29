package fr.datadome.modules.api

import fr.datadome.modules.detection.DetectionMethods
import fr.datadome.modules.fio.FileIO
import fr.datadome.modules.utils._
import cats.effect.IO
import fs2.Stream

import java.time.OffsetDateTime

class DetectionApi(detection: DetectionMethods, fileIO: FileIO, detectionParams: DetectionParams) {

  def repeatN(log: Log, nTimes: Long): IO[Report] = Stream[IO, Log](log).repeatN(nTimes).compile.toList.map(analyzeLogs)

  def readFromFile(path: String): IO[Report] =
    fileIO.readFile(path).compile.toList.map(analyzeLogs)

  def analyzeLogs(listOfLogs: List[Log]): Report = {
    val averageTimeReport = isAverageTimeLegal(listOfLogs, detectionParams.avgDurationOnPage)
    val requestsReport = isThereParallelCalls(
      listOfLogs,
      detectionParams.start,
      detectionParams.end,
      detectionParams.parallelFrequencySeconds,
      detectionParams.parallelTimes
    )
    val loopingReport              = isLoopingOverTheSamePage(listOfLogs, detectionParams.loopingTolerated)
    val suspiciousUserAgentsReport = isSuspiciousUserAgent(listOfLogs)
    val isSuspicious =
      defineIsSuspicious(averageTimeReport, requestsReport, loopingReport, suspiciousUserAgentsReport)

    val score = defineScore(averageTimeReport, requestsReport, loopingReport, suspiciousUserAgentsReport)

    Report(
      ip = listOfLogs.head.ip,
      averageTimeReport = averageTimeReport,
      requestsReport = requestsReport,
      loopingReport = loopingReport,
      suspiciousUserAgentsReport = suspiciousUserAgentsReport,
      maybeBot = isSuspicious,
      scorePercentage = score
    )
  }

  private def isAverageTimeLegal(listOfLogs: List[Log], legal: Float): AverageTimeReport = {
    val avgDurationBetweenCalls = detection.avgDurationBetweenCalls(listOfLogs.map(_.time))
    val isSuspicious            = avgDurationBetweenCalls < legal
    AverageTimeReport(avgDurationBetweenCalls, isSuspicious)
  }

  private def isThereParallelCalls(
      listOfLogs: List[Log],
      start: OffsetDateTime,
      end: OffsetDateTime,
      frequencySeconds: Long,
      times: Int
  ): RequestsReport = {
    val mapOfCalls =
      detection.parallelCalls(listOfLogs.map(_.time), start, end, frequencySeconds).filter(res => res._2 >= times)
    val isSuspicious = mapOfCalls.nonEmpty
    RequestsReport(mapOfCalls, isSuspicious)
  }

  private def isLoopingOverTheSamePage(listOfLogs: List[Log], tolerated: Long): LoopingReport = {
    val mapOfCallsCount = detection.theSamePageIsCalledSuccessifly(listOfLogs, tolerated)
    val isSuspicious    = mapOfCallsCount.nonEmpty
    LoopingReport(mapOfCallsCount, isSuspicious)
  }

  private def isSuspiciousUserAgent(listOfLogs: List[Log]): SuspiciousUserAgentsReport = {
    val listOfSuspiciousAgent = detection.analyzeUserAgent(listOfLogs, "bot")
    val isSuspicious          = listOfSuspiciousAgent.nonEmpty
    SuspiciousUserAgentsReport(listOfSuspiciousAgent, isSuspicious)
  }

  private def defineIsSuspicious(
      averageTimeReport: AverageTimeReport,
      requestsReport: RequestsReport,
      loopingReport: LoopingReport,
      suspiciousUserAgentsReport: SuspiciousUserAgentsReport
  ): Boolean =
    averageTimeReport.isSuspicious & requestsReport.isSuspicious & loopingReport.isSuspicious & suspiciousUserAgentsReport.isSuspicious

  private def defineScore(
      averageTimeReport: AverageTimeReport,
      requestsReport: RequestsReport,
      loopingReport: LoopingReport,
      suspiciousUserAgentsReport: SuspiciousUserAgentsReport
  ): Long =
    (((averageTimeReport.isSuspicious.compare(false) +
      requestsReport.isSuspicious.compareTo(false) +
      loopingReport.isSuspicious.compareTo(false) +
      suspiciousUserAgentsReport.isSuspicious.compare(false)) / 4f) * 100).toLong

}
