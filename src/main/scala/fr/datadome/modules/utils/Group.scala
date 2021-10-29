package fr.datadome.modules.utils

import java.time.OffsetDateTime

case class Group(listOfBuckets: List[OffsetDateTime], lastLog: OffsetDateTime)
