import sbt._
import Keys._

import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "NDMailApi"

version := "0.1"

scalaVersion := "2.10.0"

resolvers += "spray" at "http://repo.spray.io/"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Repository" at "http://commons.appache.org"


libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"         % "2.3.2",
  "com.typesafe.akka"   %% "akka-slf4j"         % "2.3.2",
  "ch.qos.logback"       % "logback-classic"    % "1.1.0",
  "io.spray"             % "spray-can"          % "1.3.1",
  "io.spray"             % "spray-client"       % "1.3.1",
  "io.spray"             % "spray-routing"      % "1.3.1",
  "io.spray"            %% "spray-json"         % "1.2.6",
  "org.postgresql"       % "postgresql"         % "9.2-1003-jdbc4",
  "com.typesafe.slick"  %% "slick"              % "2.0.2",
  "joda-time"            % "joda-time"          % "2.3",
  "org.joda"             % "joda-convert"       % "1.5",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.1.0",
  "commons-daemon"       %  "commons-daemon"    % "1.0.15"
)