name := "Http"

version := "0.1"

scalaVersion := "2.13.4"

val  Http4sVersion  =  "0.21.0"
val  LogbackVersion  =  "1.2.3"
val  scalaLogging  = "3.9.2"
val  swagger =  "1.0.6"
val  ScalatraVersion = "2.7.1"
val  tapirVersion = "0.17.3"
val  sttpVersion = "3.0.0-RC10"
val  apiDoc = "0.17.4"
val  swaggerDoc = "0.17.0-M2"
val  scalaMock             = "5.1.0"
val  scalaTest             = "3.2.2"

libraryDependencies  ++=  Seq(
  "org.http4s"            %%  "http4s-blaze-server"  %  Http4sVersion,
  "org.http4s"            %%  "http4s-dsl"           %  Http4sVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLogging,
  "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % apiDoc,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % apiDoc,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % swaggerDoc,
  "org.scalamock"   %% "scalamock"   % scalaMock,
  "org.scalatest"   %% "scalatest"   % scalaTest  % "test",
  "ch.qos.logback"    %    "logback-classic"          %  LogbackVersion

)
