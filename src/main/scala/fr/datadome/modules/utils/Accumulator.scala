package fr.datadome.modules.utils

import java.time.OffsetDateTime
import io.circe.generic.auto._, io.circe.syntax._

case class Accumulator(lastDateTime: OffsetDateTime, avg: Float)
