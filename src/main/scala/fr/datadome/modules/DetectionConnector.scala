package fr.datadome.modules

import cats.effect.{ExitCode, IO, IOApp}
import fr.datadome.modules.api.DetectionApi
import fr.datadome.modules.detection.DetectionMethodsImpl
import fr.datadome.modules.fio.FileIO
import fr.datadome.modules.utils.{DetectionParams, Log}

import java.time.temporal.ChronoUnit
import java.time.{Clock, OffsetDateTime, ZoneOffset}

object DetectionConnector extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val clock: Clock = Clock.system(ZoneOffset.UTC)

    //Différents modules
    val fileIO           = new FileIO()
    val detectionMethods = new DetectionMethodsImpl()

    // Déclaration de paramètres
    val detectionParams = DetectionParams(
      avgDurationOnPage = 0.5f,
      start = OffsetDateTime.now(clock),
      end = OffsetDateTime.now(clock).plus(1, ChronoUnit.HOURS),
      parallelFrequencySeconds = 2,
      parallelTimes = 3,
      loopingTolerated = 5
    )

    val detectionApi = new DetectionApi(detectionMethods, fileIO, detectionParams)

    // creation d'un log
    val time1: OffsetDateTime = OffsetDateTime.now(clock)
    val path1                 = "/product/1"
    val log                   = buildLog(time = time1, uri = path1)

    // lancer une détection sur ce log
    detectionApi
      .repeatN(log, 100)
      .attempt
      .map {
        case Left(ex)      => println("Error catched during execution " + ex)
        case Right(report) => println(" Report result: " + report.toString)
      }
      .as(ExitCode.Success)
  }

  def buildLog(
      ip: String = "0.0.0.0",
      identid: String = "-",
      userName: String = "-",
      time: OffsetDateTime = OffsetDateTime.now(),
      verb: String = "GET",
      uri: String = "/index",
      statusCode: Int = 200,
      size: Long = 12335,
      refererHeader: String = "-",
      useAgent: String = "Mozilla"
  ): Log = Log(
    ip = ip,
    identid = identid,
    userName = userName,
    time = time,
    verb = verb,
    uri = uri,
    statusCode = statusCode,
    size = size,
    refererHeader = refererHeader,
    useAgent = useAgent
  )
}
