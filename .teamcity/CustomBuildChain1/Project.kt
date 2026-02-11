package CustomBuildChain1
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object Project : Project({
    id("CustomBuildChain1")
    name = "CustomBuildChain1"

    sequence {
        build(BuildA)
        build(BuildB) // BuildB has a snapshot dependency on BuildA
    }
})

class Sequence {
    val buildTypes = arrayListOf<BuildType>()
    fun build(buildType: BuildType) {
        buildTypes.add(buildType)
    }
}

fun Project.sequence(block: Sequence.() -> Unit) {
    val sequence = Sequence().apply(block)
    var previous: BuildType? = null
    // 创建快照依赖
    for (current in sequence.buildTypes) {
        if (previous != null) {
            current.dependencies.snapshot(previous) {}
        }
        previous = current
    }
    // 对每个构建类型调用buildType函数
    // 以将其包含到当前项目中
    sequence.buildTypes.forEach(this::buildType)
}

//具体Build定义
object BuildA: BuildType({
    name="BuildA"

    vcs {
        root(DslContext.settingsRoot)
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

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})
