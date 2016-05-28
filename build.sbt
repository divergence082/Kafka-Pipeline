

lazy val kafkaPipeline = Project(
  id = "kafka-pipeline",
  base = file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    organization := "com.divergence",
    name := "kafka.pipeline",
    version := "0.0.1",
    scalaVersion := "2.11.8",
    isSnapshot := true,
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka"           % "0.10.0.0",
      "org.scalatest"    %% "scalatest"       % "3.0.0-M15" % "test")))
  .settings(
    testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Test"))))
