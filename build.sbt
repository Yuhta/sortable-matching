import sbtassembly.AssemblyPlugin.defaultShellScript

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation")

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.3.2"
)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(
  prependShellScript = Some(defaultShellScript)
)

assemblyJarName in assembly := "match"
