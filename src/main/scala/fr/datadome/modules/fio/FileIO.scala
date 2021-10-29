package fr.datadome.modules.fio

import cats.effect.{Blocker, ContextShift, IO}
import fr.datadome.modules.utils.Log
import fs2.{io, text, Pipe, Stream}

import java.nio.file.Paths
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset}
import scala.util.matching.Regex.Match

class FileIO(implicit contextShift: ContextShift[IO]) {
  //implicit val contextShift: ContextShift[IO] =
  // IO.contextShift(ExecutionContext.global) // Le rendre générale pour tout le projet

  val LOG_REGEX =
    "^(\\d.+) (\\S+) (\\S+) \\[([\\w:/]+\\s)[+-]\\d{4}\\] \"(.+?) (.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"(.+?)\"".r

  def readFile(readFrom: String): Stream[IO, Log] =
    Stream.resource(Blocker[IO]).flatMap { blocker =>
      val source: Stream[IO, Byte] = io.file.readAll[IO](Paths.get(readFrom), blocker, 4096)

      val pipe: Pipe[IO, Byte, Log] = src =>
        src
          .through(text.utf8Decode)
          .through(text.lines)
          .map { line =>
            val itSplit = LOG_REGEX.findAllMatchIn(line)
            if (itSplit.hasNext) {
              createLog(itSplit)
            } else None
          }
          .filter(_.isDefined)
          .map(_.get)

      source
        .through(pipe)
    }

  private def createLog(itSplit: Iterator[Match]): Option[Log] = {
    val split = itSplit.next()
    Some(
      Log(
        split.group(1),
        split.group(2),
        split.group(3),
        LocalDateTime
          .parse(
            split.group(4),
            DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss ")
          )
          .atOffset(ZoneOffset.UTC),
        split.group(5),
        split.group(6),
        split.group(7).toInt,
        split.group(8).toLong,
        split.group(9),
        split.group(10)
      )
    )
  }
}
