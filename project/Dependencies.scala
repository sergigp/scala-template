import sbt._

object Dependencies {

  object Version {
    val akkaHttp = "10.2.1"
    val akka     = "2.6.10"
  }

  object Production {
    val typesafeConfig     = "com.typesafe"             % "config"                   % "1.4.0"
    val akkaHttp           = "com.typesafe.akka"        %% "akka-http"               % "10.1.11"
    val playJson           = "com.typesafe.play"        %% "play-json"               % "2.8.1"
    val joda               = "joda-time"                % "joda-time"                % "2.9.9"
    val akkaHttpPlayJson   = "de.heikoseeberger"        %% "akka-http-play-json"     % "1.30.0"
    val quasar             = "com.sergigp.quasar"       %% "quasar"                  % "0.8.4"
    val playJsonJoda       = "com.typesafe.play"        %% "play-json-joda"          % "2.7.4"
    val wsClient           = "com.typesafe.play"        %% "play-ahc-ws-standalone"  % "2.1.2"
    val playWsJson         = "com.typesafe.play"        %% "play-ws-standalone-json" % "2.1.2"
    val cats               = "org.typelevel"            %% "cats-core"               % "2.1.0"
    val hikariCP           = "com.typesafe.slick"       %% "slick-hikaricp"          % "3.3.2"
    val jodaMapper         = "com.github.tototoshi"     %% "slick-joda-mapper"       % "2.4.2"
    val scopt              = "com.github.scopt"         %% "scopt"                   % "3.7.1"
    val akkaHttpCors       = "ch.megard"                %% "akka-http-cors"          % "0.4.2"
    val enumeratum         = "com.beachape"             %% "enumeratum"              % "1.6.1"
    val enumeratumSlick    = "com.beachape"             %% "enumeratum-slick"        % "1.6.0"
    val enumeratumPlayJson = "com.beachape"             %% "enumeratum-play-json"    % "1.6.0"
    val log4jApi           = "org.apache.logging.log4j" % "log4j-api"                % "2.11.0"
    val log4jCore          = "org.apache.logging.log4j" % "log4j-core"               % "2.11.0" % Runtime
    val logstashLayout     = "com.vlkan.log4j2"         % "log4j2-logstash-layout"   % "0.15"
  }

  object Test {
    val scalatest         = "org.scalatest"     %% "scalatest"           % "3.0.8"          % "test"
    val scalaMock         = "org.scalamock"     %% "scalamock"           % "4.4.0"          % "test"
    val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka     % "test"
    val akkaHttpTestkit   = "com.typesafe.akka" %% "akka-http-testkit"   % Version.akkaHttp % "test"
  }
}
