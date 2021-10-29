package fr.datadome.unit.io

import cats.effect.{ContextShift, IO}
import fr.datadome.modules.fio.FileIO
import fr.datadome.unit.ModelBuilder
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime
import scala.concurrent.ExecutionContext

class FileIOSpecs extends AnyWordSpec with Matchers {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  "FileIO" should {
    "read file and return a list of element in file" in {

      val reader = new FileIO()
      val listOfLogs = reader
        .readFile("itTestLog")
        .compile
        .toList
        .unsafeRunSync()
      listOfLogs.size shouldBe 10

      val log = ModelBuilder.buildLog(
        ip = "13.66.139.0",
        time = OffsetDateTime.parse("2020-12-19T13:57:26Z"),
        uri = "/index.php?option=com_phocagallery&view=category&id=1:almhuette-raith&Itemid=53 HTTP/1.1",
        size = 32653,
        useAgent = "Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)"
      )

      listOfLogs.head shouldBe log

    }
  }

}
