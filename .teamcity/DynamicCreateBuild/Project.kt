package DynamicCreateBuild
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildSteps.maven

val operatingSystems = listOf("Mac OS X", "Windows", "Linux")
val jdkVersions = listOf("JDK_18", "JDK_11")

object Project : Project({
    id("DynamicCreateBuild")
    name = "DynamicCreateBuild"
    
    for (os in operatingSystems) {
        for (jdk in jdkVersions) {
            buildType(Build(os, jdk))
        }
    }
})

class Build(val os: String, val jdk: String) : BuildType({
    id("Build_${os.replace(" ", "_")}_${jdk}")
    name = "Build ($os, $jdk)"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean package"
            mavenVersion = defaultProvidedVersion()
            jdkHome = "%env.${jdk}%"
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", os)
    }
})
