val scala3Version = "3.3.1"

lazy val zioVersion = "2.0.19"
lazy val zioJsonVersion = "0.5.0"
lazy val sttpVersion = "3.9.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-adventofcode-2023",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-json" % zioJsonVersion,
      "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
      "com.softwaremill.sttp.client3" %% "zio" % sttpVersion,
      "com.softwaremill.sttp.client3" %% "zio-json" % sttpVersion,
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    Test / fork := true,
    run / fork := true
  )
