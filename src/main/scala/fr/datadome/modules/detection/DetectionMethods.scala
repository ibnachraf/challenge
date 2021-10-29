package fr.datadome.modules.detection
import fr.datadome.modules.utils.{Accumulator, Group, Log, Successifly}

import java.time.{Instant, OffsetDateTime, ZoneOffset}
import scala.collection.{immutable, mutable}

trait DetectionMethods {
  this: DetectionMethodsImpl =>

  def avgDurationBetweenCalls(list: List[OffsetDateTime]): Float = {
    val start = list.tail.head.toEpochSecond - list.head.toEpochSecond
    list.tail
      .foldLeft(Accumulator(list.head, start)) { (acc, curr) =>
        val deltaSeconds  = curr.toEpochSecond - acc.lastDateTime.toEpochSecond
        val newAvgSeconds = (acc.avg + deltaSeconds) / 2
        Accumulator(curr, newAvgSeconds)
      }
      .avg
  }

  def parallelCalls(
      list: List[OffsetDateTime],
      start: OffsetDateTime,
      end: OffsetDateTime,
      frequency: Long
  ): immutable.Map[OffsetDateTime, Int] = {
    val numberOfBuckets = ((end.toEpochSecond - start.toEpochSecond) / frequency.floatValue).ceil.toLong

    val buckets = List
      .range(0, numberOfBuckets)
      .foldLeft(Group(List(), start)) { (acc, _) =>
        val nextBucket =
          OffsetDateTime.ofInstant(Instant.ofEpochSecond(acc.lastLog.toEpochSecond + frequency), ZoneOffset.UTC)
        Group(acc.listOfBuckets.appended(acc.lastLog), nextBucket)
      }
      .listOfBuckets

    groupCallsByBucket(list, buckets)
  }

  def theSamePageIsCalledSuccessifly(listOfLogs: List[Log], tolerated: Long) = {
    val init = Successifly(new immutable.HashMap[String, Long](), listOfLogs.head.uri, 0)
    listOfLogs.tail.foldLeft(init) { (acc, log) =>
      if (acc.lastPath.equals(log.uri)) {
        val newCount = acc.count + 1
        Successifly(acc.map + (acc.lastPath -> newCount), log.uri, newCount)
      } else if (!acc.lastPath.equals(log.uri) & acc.count > tolerated) {
        val newCount = acc.count + 1
        Successifly(acc.map + (acc.lastPath -> newCount), log.uri, 0)
      } else
        Successifly(acc.map, log.uri, 0)
    }
  }.map

  def analyzeUserAgent(listOfLogs: List[Log], sequence: String): List[String] =
    listOfLogs.filter(_.useAgent.contains(sequence)).map(_.ip)

}
