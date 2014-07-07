addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.5.0")

addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.3.5")

resolvers += Classpaths.typesafeResolver
 
resolvers ++= Seq(
	"snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
	"typesafe" at "http://repo.typesafe.com/typesafe/releases" 
)

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "latest.integration")
 
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")
