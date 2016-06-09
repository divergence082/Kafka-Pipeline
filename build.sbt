

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := (
    <url>https://github.com/divergence082/Kafka-Pipeline</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://www.opensource.org/licenses/bsd-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:divergence082/Kafka-Pipeline.git</url>
        <connection>scm:git:git@github.com:divergence082/Kafka-Pipeline.git</connection>
        <developerConnection>scm:git:git@github.com:divergence082/Kafka-Pipeline.git</developerConnection>
      </scm>
      <developers>
        <developer>
          <id>divergence082</id>
          <name>Valeria Kononenko</name>
          <email>divergence082@gmail.com</email>
          <url>http://divergence.space</url>
        </developer>
      </developers>)
)


lazy val kafkaPipeline = Project(
  id = "kafka-pipeline",
  base = file("."),
  settings = Defaults.coreDefaultSettings ++ Defaults.itSettings ++ publishSettings ++ Seq(
    organization := "space.divergence",
    name := "kafka-pipeline",
    version := "0.0.1",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka" % "0.10.0.0"
        exclude("org.slf4j", "slf4j-log4j12")
        exclude("javax.jms", "jms")
        exclude("com.sun.jdmk", "jmxtools")
        exclude("com.sun.jmx", "jmxri"),
      "ch.qos.logback"   %  "logback-classic" % "1.1.7",
      "org.scalatest"    %% "scalatest"       % "3.0.0-RC1" % "test")))
  .configs(IntegrationTest)
  .settings(
    testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Test"))))
  .settings(
    scalastyleConfig in Compile := baseDirectory.value / "project" / "scalastyle-config.xml",
    scalastyleConfig in Test := baseDirectory.value / "project" / "scalastyle-config.xml"
  )
