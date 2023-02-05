import sbt._

object Dependencies {

    val fs2Version        = "1.0.0"
    val catsVersion       = "1.4.0"
    val catsEffectVersion = "1.0.0"

    val backendDeps = Def.setting(
        Seq(
            "com.lihaoyi"          %% "sourcecode"      % "0.1.4",            // Expert println debugging
            "com.lihaoyi"          %% "pprint"          % "0.5.3",            // Pretty print for types and case classes
            "com.lihaoyi"          %% "fansi"           % "0.2.5",
            "org.typelevel"        %% "cats-core"       % catsVersion,        // Abstract category dork stuff
            "org.typelevel"        %% "cats-effect"     % catsEffectVersion,  // IO monad category wank
            "com.chuusai"          %% "shapeless"       % "2.3.2",            // Abstract level category dork stuff
            "org.tpolecat"         %% "atto-core"       % "0.6.3",            // For parsing asm
            "org.tpolecat"         %% "atto-refined"    % "0.6.3",            // For parsing asm
            "com.github.pathikrit" %% "better-files"    % "3.7.0",
            "org.atnos"            %% "eff"             % "5.2.0",
            "com.slamdata"         %% "matryoshka-core" % "0.21.3"
        )
    )
}
