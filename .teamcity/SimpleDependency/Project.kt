package SimpleDependency
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("SimpleDependency")
    name = "SimpleDependency"

    buildType(Package)
    buildType(Publish)
})

object Package : BuildType({
    name = "Package"
    artifactRules = "application.zip"
    steps {
        // 定义生成application.zip所需的步骤
    }
})

object Publish: BuildType({
    name="Publish"
    steps {
        // 定义发布制品所需的步骤
    }
    dependencies {
        snapshot(Package) {}
        artifacts(Package) {
            artifactRules = "application.zip"
        }
    }
})
