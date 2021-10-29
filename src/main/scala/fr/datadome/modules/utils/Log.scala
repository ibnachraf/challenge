package fr.datadome.modules.utils

import java.time.{LocalDateTime, OffsetDateTime}

case class Log(
    ip: String,
    identid: String,
    userName: String,
    time: OffsetDateTime,
    verb: String,
    uri: String,
    statusCode: Int,
    size: Long,
    refererHeader: String,
    useAgent: String
)
