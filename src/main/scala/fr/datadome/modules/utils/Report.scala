package fr.datadome.modules.utils

import io.circe._, io.circe.generic.semiauto._

import java.time.OffsetDateTim
case class Report(
    ip: String,
    averageTimeReport: AverageTimeReport,
    requestsReport: RequestsReport,
    loopingReport: LoopingReport,
    suspiciousUserAgentsReport: SuspiciousUserAgentsReport,
    maybeBot: Boolean,
    scorePercentage: Long
)

case class AverageTimeReport(avgDurationBetweenCalls: Float, isSuspicious: Boolean)
object AverageTimeReport {
  implicit val AverageTimeReportDecoder: Decoder[AverageTimeReport] = deriveDecoder[AverageTimeReport]
  implicit val AverageTimeReportEncoder: Encoder[AverageTimeReport] = deriveEncoder[AverageTimeReport]
}

case class RequestsReport(calls: Map[OffsetDateTime, Int], isSuspicious: Boolean)

case class LoopingReport(mapOfCallsCount: Map[String, Long], isSuspicious: Boolean)
object LoopingReport {
  implicit val LoopingReportDecoder: Decoder[LoopingReport] = deriveDecoder
  implicit val LoopingReportEncoder: Encoder[LoopingReport] = deriveEncoder[LoopingReport]
}

case class SuspiciousUserAgentsReport(userAgents: List[String], isSuspicious: Boolean)
object SuspiciousUserAgentsReport {
  implicit val fooDecoder: Decoder[SuspiciousUserAgentsReport] = deriveDecoder
  implicit val fooEncoder: Encoder[SuspiciousUserAgentsReport] = deriveEncoder[SuspiciousUserAgentsReport]
}
