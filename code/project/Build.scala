import sbt._
import Keys._

import Helpers._
import Dependencies._

object XsbtBatsh extends Build {
  lazy val root = Project(
    "XSBT-BatSh",
    file("."),
    settings = 
      Default.settings :+ 
      libDeps(
        //test
        scalaTest
      )
  )
}

//  ---------------------------------------------------------------------------

object Default {
  val settings =
    Defaults.defaultSettings ++
    Resolvers.settings ++
    Publishing.settings ++
    Format.settings ++ Seq(
      organization := "hr.element.etb",
      crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.8.2", "2.8.1"),
      scalaVersion <<= (crossScalaVersions) { versions => versions.head },
      scalacOptions := Seq("-unchecked", "-deprecation", "-optimise", "-encoding", "UTF8"),
      javacOptions  := Seq("-deprecation", "-encoding", "UTF-8", "-source", "1.6", "-target", "1.6")
    )
}

//  ---------------------------------------------------------------------------

object Dependencies {
  // Testing
  val scalaTest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
}

//  ---------------------------------------------------------------------------

object Repositories {
  val ElementNexus     = "Element Nexus"     at "http://maven.element.hr/nexus/content/groups/public/"
  val ElementReleases  = "Element Releases"  at "http://maven.element.hr/nexus/content/repositories/releases/"
  val ElementSnapshots = "Element Snapshots" at "http://maven.element.hr/nexus/content/repositories/snapshots/"
}

object Resolvers {
  import Repositories._

  val settings = Seq(
    resolvers := Seq(ElementNexus, ElementReleases, ElementSnapshots),
    externalResolvers <<= resolvers map { rs =>
      Resolver.withDefaultResolvers(rs, mavenCentral = false, scalaTools = false)
    }
  )
}

object Publishing {
  import Repositories._

  val settings = Seq(
    publishTo <<= (version) { version => Some(
      if (version.endsWith("SNAPSHOT")) ElementSnapshots else ElementReleases
    )},
    credentials += Credentials(Path.userHome / ".publish" / "element.credentials"),
    publishArtifact in (Compile, packageDoc) := false
  )
} 

//  ---------------------------------------------------------------------------

object Format {
  // Scalariform plugin
  import com.typesafe.sbtscalariform._
  import ScalariformPlugin._
  import scalariform.formatter.preferences._

  ScalariformKeys.preferences := FormattingPreferences()
    .setPreference(AlignParameters, false)
    .setPreference(AlignSingleLineCaseStatements, false)
    .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
    .setPreference(CompactControlReadability, true)
    .setPreference(CompactStringConcatenation, false)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(FormatXml, false)
    .setPreference(IndentLocalDefs, false)
    .setPreference(IndentPackageBlocks, false)
    .setPreference(IndentSpaces, 2)
    .setPreference(IndentWithTabs, false)
    .setPreference(MultilineScaladocCommentsStartOnFirstLine, true)
    .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
    .setPreference(PreserveDanglingCloseParenthesis, false)
    .setPreference(PreserveSpaceBeforeArguments, false)
    .setPreference(RewriteArrowSymbols, false)
    .setPreference(SpaceBeforeColon, false)
    .setPreference(SpaceInsideBrackets, false)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(SpacesWithinPatternBinders, true)

  lazy val settings =
    ScalariformPlugin.defaultScalariformSettings
}

//  ---------------------------------------------------------------------------

object Helpers {
  implicit def depToFunSeq(m: ModuleID) = Seq((_: String) => m)
  implicit def depFunToSeq(fm: String => ModuleID) = Seq(fm)
  implicit def depSeqToFun(mA: Seq[ModuleID]) = mA.map(m => ((_: String) => m))
  
  def libDeps(deps: (Seq[String => ModuleID])*) = {
    libraryDependencies <++= scalaVersion( sV =>
      for (depSeq <- deps; dep <- depSeq) yield dep(sV)
    )
  }
}