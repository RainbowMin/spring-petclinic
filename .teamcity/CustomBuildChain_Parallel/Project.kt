package CustomBuildChain_Parallel
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("CustomBuildChain_Parallel")
    name = "CustomBuildChain_Parallel"

    sequence {
        build(Compile)
        parallel {
            build(Test1)
            sequence {
                build(Test2)
                build(Test3)
            }
        }
        build(Package1)
        build(Publish1)
    }
})

interface Stage

class Single(val buildType: BuildType) : Stage

class Parallel : Stage {
    val buildTypes = arrayListOf<BuildType>()

    fun build(buildType: BuildType) {
        buildTypes.add(buildType)
    }
}

class Sequence {
    val stages = arrayListOf<Stage>()

    fun build(buildType: BuildType) {
        stages.add(Single(buildType))
    }

    fun parallel(block: Parallel.() -> Unit) {
        val parallel = Parallel().apply(block)
        stages.add(parallel)
    }
}

fun Project.sequence(block: Sequence.() -> Unit) {
    val sequence = Sequence().apply(block)

    var previous: Stage? = null

    for (current in sequence.stages) {
        if (previous != null) {
            createSnapshotDependency(current, previous)
        }
        previous = current
    }

    sequence.stages.forEach {
        if (it is Single) {
            buildType(it.buildType)
        }
        if (it is Parallel) {
            it.buildTypes.forEach(this::buildType)
        }
    }
}

fun Project.parallel(block: Parallel.() -> Unit) {
    val parallel = Parallel().apply(block)
    parallel.buildTypes.forEach(this::buildType)
}

fun createSnapshotDependency(stage: Stage, dependency: Stage){
    if (dependency is Single) {
        stageDependsOnSingle(stage, dependency)
    }
    if (dependency is Parallel) {
        stageDependsOnParallel(stage, dependency)
    }
}

fun stageDependsOnSingle(stage: Stage, dependency: Single) {
    if (stage is Single) {
        singleDependsOnSingle(stage, dependency)
    }
    if (stage is Parallel) {
        parallelDependsOnSingle(stage, dependency)
    }
}

fun stageDependsOnParallel(stage: Stage, dependency: Parallel) {
    if (stage is Single) {
        singleDependsOnParallel(stage, dependency)
    }
    if (stage is Parallel) {
        throw IllegalStateException("Parallel cannot snapshot-depend on parallel")
    }
}

fun parallelDependsOnSingle(stage: Parallel, dependency: Single) {
    stage.buildTypes.forEach { buildType ->
        singleDependsOnSingle(Single(buildType), dependency)
    }
}

fun singleDependsOnParallel(stage: Single, dependency: Parallel) {
    dependency.buildTypes.forEach { buildType ->
        singleDependsOnSingle(stage, Single(buildType))
    }
}

fun singleDependsOnSingle(stage: Single, dependency: Single) {
    stage.buildType.dependencies.snapshot(dependency.buildType) {}
}

//具体Build定义
object Compile: BuildType({
    name="Compile"

    vcs {
        root(DslContext.settingsRoot)
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})

object Test1: BuildType({
    name="Test1"

    vcs {
        root(DslContext.settingsRoot)
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})

object Test2: BuildType({
    name="Test2"

    vcs {
        root(DslContext.settingsRoot)
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})
object Test3: BuildType({
    name="Test3"

    vcs {
        root(DslContext.settingsRoot)
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})
object Package1: BuildType({
    name="Package1"

    vcs {
        root(DslContext.settingsRoot)
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})

object Publish1: BuildType({
    name="Publish1"

    vcs {
        root(DslContext.settingsRoot)
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})
