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
        id("com.diffplug.spotless") version "5.14.3"
        id("org.jetbrains.dokka") version "1.6.0"
    }
}

include(
    ":sample",
    ":livecallback"
)
