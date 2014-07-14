import play.Project._

name := "play2"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq( jdbc, anorm )

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "com.assembla.scala-incubator" % "graph-core_2.10.0-RC5" % "1.5.2"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.18"

// libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.3" % "test"

// libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.3"
