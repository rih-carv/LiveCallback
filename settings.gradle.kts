rootProject.name = "LiveCallback"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

pluginManagement {
    plugins {
        kotlin("jvm") version "1.5.31"
        id("com.diffplug.spotless") version "6.7.2"
        id("org.jetbrains.dokka") version "1.5.31"
    }
}

include(
    ":sample",
    ":livecallback"
)
