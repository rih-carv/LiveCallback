// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath(embeddedKotlin("gradle-plugin"))
    }
}

plugins {
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            ktlint()
        }
        kotlinGradle {
            ktlint()
        }
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
        if (System.getenv("CI") == "true") kotlinOptions.allWarningsAsErrors = true
    }
}

val liveCallbackVersion by extra("1.0.0")
val lifecycleVersion by extra("2.4.0")
val junitVersion by extra("4.13.2")
