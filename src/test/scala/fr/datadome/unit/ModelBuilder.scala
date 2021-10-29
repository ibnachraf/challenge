package fr.datadome.unit

import fr.datadome.modules.utils.Log

import java.time.OffsetDateTime

object ModelBuilder {

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
