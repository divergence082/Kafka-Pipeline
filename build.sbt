

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
      "org.apache.kafka" %% "kafka"           % "0.10.0.0"
        exclude("org.slf4j", "slf4j-log4j12")
        exclude("javax.jms", "jms")
        exclude("com.sun.jdmk", "jmxtools")
        exclude("com.sun.jmx", "jmxri"),
      "ch.qos.logback"   %  "logback-classic" % "1.1.7",
      "org.scalatest"    %% "scalatest"       % "3.0.0-RC1" % "test")))
  .settings(
    testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Test"))))
  .settings(
    scalastyleConfig in Compile := baseDirectory.value / "project" / "scalastyle-config.xml",
    scalastyleConfig in Test := baseDirectory.value / "project" / "scalastyle-config.xml"
  )
