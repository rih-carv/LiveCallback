rootProject.name = "LiveCallback"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.5.30"
        id("com.diffplug.spotless") version "5.14.3"
        id("org.jetbrains.dokka") version ("1.5.0")
    }
}

include(
    ":sample",
    ":livecallback"
)
