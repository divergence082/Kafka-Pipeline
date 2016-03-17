

lazy val kafkaPipeline = Project(
  id = "kafka-pipeline",
  base = file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    organization := "com.divergence",
    name := "kafka",
    version := "1.0",
    scalaVersion := "2.11.8",
    isSnapshot := true,
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka"           % "0.9.0.1",
      "com.typesafe"     %  "config"          % "1.3.0",
      "org.scalatest"    %% "scalatest"       % "3.0.0-M15" % "it,test")))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings : _*)
  .settings(
    testOptions in IntegrationTest := Seq(Tests.Filter(s => s.endsWith("Test"))))
