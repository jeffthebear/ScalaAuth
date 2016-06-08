name := """scala-auth"""

version := "1.0-SNAPSHOT"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val appDependencies = Seq(
  "org.postgresql"    %  "postgresql"        % "9.4-1201-jdbc41",
  javaJdbc,
  cache,
  javaWs,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.nimbusds" % "nimbus-jose-jwt" % "4.11.2",
  "be.objectify"  %% "deadbolt-java"     % "2.5.0",
  "com.feth"      %% "play-authenticate" % "0.8.1-SNAPSHOT",
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.easytesting" % "fest-assert" % "1.4" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.52.0" % "test"
)

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  Resolver.sonatypeRepo("snapshots")
)
//resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
//resolvers += Resolver.sonatypeRepo("snapshots")


lazy val root = project.in(file("."))
  .enablePlugins(PlayScala, PlayEbean)
  .settings(
    libraryDependencies ++= appDependencies
  )

fork in run := true