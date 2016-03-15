

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
      "org.apache.kafka" %% "kafka" % "0.9.0.1"
    )
  )
)
