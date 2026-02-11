package SimpleSequence

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object Project : Project({
    id("SimpleSequence")
    name = "SimpleSequence"

    buildType(BuildA)
    buildType(BuildB)
    buildType(BuildC)
})

object BuildA: BuildType({
    name="BuildA"

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

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})

object BuildB: BuildType({
    name="BuildB"

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

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})

object BuildC: BuildType({
    name="BuildC"

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

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})
