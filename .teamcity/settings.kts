import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.03"

project {
    buildType(cleanFiles(MinjieBuildConfigOfSpringPetclinic))
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

/*
这个新功能本质上接收一个BuildType，为其添加一个特性，然后返回该BuildType。
由于Kotlin支持顶层函数（即不需要对象或类来承载函数），
我们可以将它放在代码中的任何位置，或者创建一个专门的文件来存放它。
 */
fun cleanFiles(buildType: BuildType): BuildType {
    if (buildType.features.items.find { it.type == "swabra" } == null) {
        buildType.features {
            swabra {
            }
        }
    }
    return buildType
}

/*
将其泛化，以便自己定义功能:创建所谓的高阶函数，即一种将另一个函数作为参数的函数。实际上，这正是features、feature以及TeamCity领域特定语言（DSL）中的许多其他元素的本质。
*/
fun wrapWithFeature(buildType: BuildType, featureBlock: BuildFeatures.() -> Unit): BuildType {
    buildType.features {
        featureBlock()
    }
    return buildType
}
