package fr.datadome.integration

import cats.effect.{ContextShift, IO}
import fr.datadome.modules.api.DetectionApi
import fr.datadome.modules.detection.DetectionMethodsImpl
import fr.datadome.modules.fio.FileIO
import fr.datadome.modules.utils.{AverageTimeReport, DetectionParams}
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{Clock, Instant, OffsetDateTime, ZoneOffset}
import scala.concurrent.ExecutionContext

class DetectionApiIT extends AnyWordSpec with Matchers with GivenWhenThen {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  implicit val clock: Clock = Clock.fixed(Instant.parse("2020-12-19T13:57:26Z"), ZoneOffset.UTC)

  "DetectionAPi" should {
    "detect a bot" in {

      Given("Detection parameters")
      val detectionParams = DetectionParams(
        avgDurationOnPage = 0.5f,
        start = OffsetDateTime.now(clock),
        end = OffsetDateTime.parse("2020-12-19T13:58:26Z"),
        parallelFrequencySeconds = 2,
        parallelTimes = 3,
        loopingTolerated = 5
      )

      And("Detection API")
      val fileIO           = new FileIO()
      val detectionMethods = new DetectionMethodsImpl()
      val detectionApi     = new DetectionApi(detectionMethods, fileIO, detectionParams)

      When("detection start")
      val res = detectionApi.readFromFile("itTestLog").unsafeRunSync()

      Then("the average during between calls")
      res.averageTimeReport shouldBe AverageTimeReport(0.25f, true)

      And("request report")
      val entry4 = OffsetDateTime.parse("2020-12-19T13:57:32Z") -> 4
      res.requestsReport.calls should contain(entry4)
      res.requestsReport.isSuspicious shouldBe true

      And("Looping Report")
      val uri   = "/index.php?option=com_phocagallery&view=category&id=1:almhuette-raith&Itemid=53 HTTP/1.1"
      val entry = uri -> 10
      res.loopingReport.mapOfCallsCount should have size 1
      res.loopingReport.mapOfCallsCount should contain(entry)
      res.loopingReport.isSuspicious shouldBe true

      And("SuspiciousUserAgentsReport contains the sequence bot")
      res.suspiciousUserAgentsReport.isSuspicious shouldBe true

      And("Score")
      res.scorePercentage shouldBe 100
    }
  }

}
