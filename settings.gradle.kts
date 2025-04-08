pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CropKit"
val jitpackEnv: String? = System.getenv("JITPACK")
if (jitpackEnv.isNullOrEmpty() || !jitpackEnv.toBoolean()) {
    include(":app")
}
include(":cropkit")
