import sbt._
import sbt.Keys._

name := "scala-template"

version := "0.1"

scalaVersion := "2.13.3"

scalaSource in Compile := baseDirectory.value / "src/main/"
scalaSource in Test := baseDirectory.value / "src/test/"
resourceDirectory in Compile := baseDirectory.value / "resources"

resolvers += "central" at "https://repo.maven.apache.org/maven2/"
resolvers += Resolver.bintrayRepo("sergigp", "maven")

scalacOptions ++= Seq("-encoding", "UTF-8")
