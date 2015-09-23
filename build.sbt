name := """FoodTruckAPI"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "2.2.7",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.jsoup" % "jsoup" % "1.8.3",
  "mysql" % "mysql-connector-java" % "5.1.36"
)