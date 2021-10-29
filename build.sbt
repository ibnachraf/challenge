name := "datadome_challenge"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"

libraryDependencies += "co.fs2" %% "fs2-core" % "2.5.10"

// optional I/O library
libraryDependencies += "co.fs2" %% "fs2-io" % "2.5.10"

// optional reactive streams interop
libraryDependencies += "co.fs2" %% "fs2-reactive-streams" % "2.5.10"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % "0.14.1")
