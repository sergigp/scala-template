import sbt._
import sbt.Keys._

name := "template"

version := "0.1"

scalaVersion := "2.13.3"

scalaSource in Compile := baseDirectory.value / "src/main/"
scalaSource in Test := baseDirectory.value / "src/test/"
resourceDirectory in Compile := baseDirectory.value / "resources"

resolvers += "central" at "https://repo.maven.apache.org/maven2/"
resolvers += Resolver.bintrayRepo("sergigp", "maven")

scalacOptions ++= Seq("-encoding", "UTF-8")

libraryDependencies ++= Seq(
  Dependencies.Production.typesafeConfig,
  Dependencies.Production.akkaHttp,
  Dependencies.Production.playJson,
  Dependencies.Production.joda,
  Dependencies.Production.akkaHttpPlayJson,
  Dependencies.Production.quasar,
  Dependencies.Production.playJsonJoda,
  Dependencies.Production.wsClient,
  Dependencies.Production.playWsJson,
  Dependencies.Production.cats,
  Dependencies.Production.hikariCP,
  Dependencies.Production.jodaMapper,
  Dependencies.Production.scopt,
  Dependencies.Production.akkaHttpCors,
  Dependencies.Production.enumeratum,
  Dependencies.Production.enumeratumPlayJson,
  Dependencies.Production.enumeratumSlick,
  Dependencies.Production.log4jApi,
  Dependencies.Production.log4jCore,
  Dependencies.Production.logstashLayout,
  /** TEST DEPENDENCIES **/
  Dependencies.Test.scalatest,
  Dependencies.Test.scalaMock,
  Dependencies.Test.akkaHttpTestkit,
  Dependencies.Test.akkaStreamTestkit,
)

Test / fork := true
cancelable in Global := true
parallelExecution in Test := false
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

enablePlugins(JavaAppPackaging)

/** *****************************************/
/** ************** ALIASES ******************/
/** *****************************************/
addCommandAlias("c", "compile")
addCommandAlias("s", "scalastyle")
addCommandAlias("tc", "test:compile")
addCommandAlias("ts", "test:scalastyle")
addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
addCommandAlias("prep", ";scalastyle;test:scalastyle;scalafmtCheck;test:scalafmtCheck")
