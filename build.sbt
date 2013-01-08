libraryDependencies ++= Seq( 
	"net.databinder" %% "dispatch-core" % "0.8.8",
	"net.databinder" %% "dispatch-http" % "0.8.8"//,
	//"net.databinder" %% "dispatch-lift-json" % "0.8.8" intransitive()//,
	//"net.liftweb" %% "lift-json" % "2.4"
)

resolvers ++= Seq(
	"Sonatype latest" at "https://oss.sonatype.org/service/local/repositories/releases/content/",
	"Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)
