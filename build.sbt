name := """play-reactivemongo-oauth2-provider"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.nulab-inc" %% "play2-oauth2-provider" % "0.17.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.1",
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test)

fork in run := true