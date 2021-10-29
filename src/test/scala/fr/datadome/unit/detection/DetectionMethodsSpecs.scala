package fr.datadome.unit.detection

import fr.datadome.modules.detection.DetectionMethodsImpl
import fr.datadome.unit.ModelBuilder.buildLog
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time._
import java.time.temporal.ChronoUnit

class DetectionMethodsSpecs extends AnyWordSpec with Matchers {

  implicit val clock: Clock = Clock.fixed(Instant.parse("2021-11-01T00:00:00Z"), ZoneOffset.UTC)

  private val time1: OffsetDateTime = OffsetDateTime.now(clock)
  private val time2: OffsetDateTime = time1.plus(2000, ChronoUnit.MILLIS)
  private val time3: OffsetDateTime = time2.plus(2000, ChronoUnit.MILLIS)
  private val time4: OffsetDateTime = time3.plus(1000, ChronoUnit.MILLIS)
  private val time5: OffsetDateTime = time4
  private val time6: OffsetDateTime = time5.plus(1, ChronoUnit.MINUTES)

  "developed detection methods" should {
    "calculate the avg time of naviguation" in {
      val calculation = new DetectionMethodsImpl()
      val listOfTimes = List(time1, time2, time3)
      calculation.avgDurationBetweenCalls(listOfTimes) shouldBe 2
    }

    "calculate number of calls in period of time" in {
      val calculation = new DetectionMethodsImpl()
      val listOfTimes = List(time1, time2, time3, time4, time5, time6)
      val group       = calculation.parallelCalls(listOfTimes, time1, time6, 4).filter(res => res._2 != 0)
      val entry1      = OffsetDateTime.parse("2021-11-01T00:00:04Z") -> 3
      val entry2      = OffsetDateTime.parse("2021-11-01T00:00Z")    -> 2
      val entry3      = OffsetDateTime.parse("2021-11-01T00:01:04Z") -> 1
      group should have size 3
      group should contain(entry1)
      group should contain(entry2)
      group should contain(entry3)
    }

    "detect if a bot is calling teh same page every time" in {
      val calculation = new DetectionMethodsImpl()
      val path1       = "/product/1"
      val path2       = "/product/2"

      val log1  = buildLog(time = time1, uri = path1)
      val log2  = buildLog(time = time2, uri = path1)
      val log3  = buildLog(time = time3, uri = path1)
      val log4  = buildLog(time = time4, uri = path1)
      val log5  = buildLog(time = time5, uri = path1)
      val log6  = buildLog(time = time6, uri = path1)
      val log10 = buildLog(time = time6, uri = path2)

      val botLogs = List(log1, log2, log3, log4, log5, log6, log10)
      val res     = calculation.theSamePageIsCalledSuccessifly(botLogs, 2)
      val entry   = path1 -> 6

      res should have size 1
      res should contain(entry)

    }

    "detect in bot by user-agent" in {
      val calculation = new DetectionMethodsImpl()

      val log1 = buildLog()
      val log  = buildLog(useAgent = "Mozilla/bot")

      calculation.analyzeUserAgent(List(log1, log), "bot").size shouldBe 1
    }
  }

}
