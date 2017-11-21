import Dependencies._

val commonSettings = Seq(
  scalacOptions ++= Seq("-unchecked", "-feature", "-explaintypes", "-deprecation")
)

lazy val root = (project in file("."))
  .settings(name := "kamon-akka-http")
  .settings(commonSettings: _*)
  .settings(Seq(
    scalaVersion := "2.11.8",
    testGrouping in Test := singleTestPerJvm((definedTests in Test).value, (javaOptions in Test).value)
  ))
  .settings(libraryDependencies ++= Seq(
    kamon.Core,
    kamon.JMX,
    kamon.KamonAutoweave,
    kamon.Akka,
    kamon.AkkaHttp,
    kamon.SystemMetrics,
    kamon.Scala,

    akka.Http,
    akka.Slf4j,
    akka.HttpCore,

    log.LogbackClassic,
    log.LogbackCore,
    log.Log4jOverSlf4j,
    log.LogstashLogback,
    log.Janino,
    log.ScalaLogging
  ))
  .settings(
    parallelExecution in Test := false,
    aspectjSettings,
    fork in run := true //ensure that the JVM is forked
  )

  // Here we are effectively adding the `-javaagent` JVM startup
  // option with the location of the AspectJ Weaver provided by
  // the sbt-aspectj plugin.
javaOptions in run <++= {AspectjKeys.weaverOptions in Aspectj}

import sbt.Tests._
def singleTestPerJvm(tests: Seq[TestDefinition], jvmSettings: Seq[String]): Seq[Group] =
  tests map { test =>
    Group(
      name = test.name,
      tests = Seq(test),
      runPolicy = SubProcess(ForkOptions(runJVMOptions = jvmSettings)))
  }
