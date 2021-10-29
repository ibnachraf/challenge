package fr.datadome.modules.detection

import java.time.OffsetDateTime
import scala.annotation.tailrec
import scala.collection.immutable
import scala.collection.immutable.HashMap

class DetectionMethodsImpl extends DetectionMethods {

  protected def groupCallsByBucket(
      logsDate: List[OffsetDateTime],
      buckets: List[OffsetDateTime]
  ): Map[OffsetDateTime, Int] =
    rec(logsDate, buckets, new immutable.HashMap[OffsetDateTime, Int]())

  @tailrec
  private def rec(
      listOfLogs: List[OffsetDateTime],
      buckets: List[OffsetDateTime],
      result: HashMap[OffsetDateTime, Int]
  ): HashMap[OffsetDateTime, Int] =
    buckets match {
      case head :: next =>
        val elementInRange =
          listOfLogs.filter(time =>
            head.toEpochSecond <= time.toEpochSecond && time.toEpochSecond < next.headOption
              .map(_.toEpochSecond)
              .getOrElse(Long.MaxValue)
          )
        result + (head                       -> elementInRange.size)
        rec(listOfLogs, next, result + (head -> elementInRange.size))

      case Nil => result
    }

}
