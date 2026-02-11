import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import SimpleSequence.Project
version = "2024.03"

project {
    subProject(SimpleSequence.Project)
    subProject(DynamicCreateBuild.Project)
    subProject(SimpleDependency.Project)
    subProject(CustomBuildChain1.Project)
    subProject(CustomBuildChain_Parallel.Project)
}

object MinjieBuildConfigOfSpringPetclinic : BuildType({
    name = "minjie Build config of Spring Petclinic"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features{
        swabra {
        }
    }

    features {
        perfmon {
        }
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})
