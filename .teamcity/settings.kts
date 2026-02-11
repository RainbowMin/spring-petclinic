import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import SimpleSequence.Project

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

/*
project {
    //buildType(cleanFiles(MinjieBuildConfigOfSpringPetclinic))
    buildType(BuildForMacOSX)
}
*/

object BuildForMacOSX : BuildType({
    name = "Build for Mac OS X"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean package"
            mavenVersion = defaultProvidedVersion()
            jdkHome = "%env.JDK_18%"
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Mac OS X")
    }
})

val operatingSystems = listOf("Mac OS X", "Windows", "Linux")
val jdkVersions = listOf("JDK_18", "JDK_11")

project {
    /*
    for (os in operatingSystems) {
        for (jdk in jdkVersions) {
            buildType(Build(os, jdk))
        }
    }
    buildType(Publish)

    sequence {
        build(BuildA)
        build(BuildB) // BuildB has a snapshot dependency on BuildA
    }
    */

    /*
    sequence {
        build(Compile)
        parallel {
            build(Test1)
            sequence {
                build(Test2)
                build(Test3)
            }
        }
        build(Package)
        build(Publish)
    }
    */

    subProject(SimpleSequence.Project)
    subProject(DynamicCreateBuild.Project)
    subProject(SimpleDependency.Project)
}

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

object Package : BuildType({
    name = "Package"

    artifactRules = "application.zip"

    steps {
        // define the steps needed to produce the application.zip
    }
})

object Publish: BuildType({
    name="Publish"

    steps {
        // define the steps needed to publish the artifacts
    }

    dependencies {
        snapshot(Package){}
        artifacts(Package) {
            artifactRules = "application.zip"
        }
    }

    requirements {
        contains("teamcity.agent.name", "minjie")
    }
})

//4.扩展TeamCity DSL
//4.1 定义一个Sequence类
/*
为Project类添加了一个扩展函数，使我们能够声明sequence。通过使用前面提到的带接收者的Lambda特性，
我们声明了用作sequence函数参数的代码块将提供Sequence类的上下文
 */
/*
fun Project.sequence(block: Sequence.()-> Unit){
    val sequence = Sequence().apply(block)
    var previous: BuildType? = null
    // 创建快照依赖
    for (current in sequence.buildTypes){
        if (previous != null){
            current.dependencies.snapshot(previous){}
        }
        previous = current
    }
    // 对每个构建类型调用buildType函数,以将其包含到当前项目中
    sequence.buildTypes.forEach(this::buildType)
}
*/
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

object Compile: BuildType({
    name="Compile"

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

object Test1: BuildType({
    name="Test1"

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

object Test2: BuildType({
    name="Test2"

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

object Test3: BuildType({
    name="Test3"

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

//4.2 Parallel
interface Stage
class Single(val buildType: BuildType) : Stage

class Parallel : Stage {
    val buildTypes = arrayListOf<BuildType>()

    fun build(buildType: BuildType) {
        buildTypes.add(buildType)
    }
}

class Sequence {
    val stages  = arrayListOf<Stage>()

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
