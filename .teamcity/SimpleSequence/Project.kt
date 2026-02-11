package SimpleSequence

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object Project : Project({
    id("SimpleSequence")
    name = "SimpleSequence"

    buildType(Build1)
    buildType(Build2)
    buildType(Build3)
})

object Build1: BuildType({
    name="Build1"

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

object Build2: BuildType({
    name="Build2"

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

object Build3: BuildType({
    name="Build3"

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
