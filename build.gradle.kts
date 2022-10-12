import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath(embeddedKotlin("gradle-plugin"))
        classpath("org.jetbrains.dokka:versioning-plugin:1.7.20")
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

    tasks.withType(KotlinCompile::class).all {
        if (System.getenv("CI") == "true") kotlinOptions.allWarningsAsErrors = true
    }
}

val dokkaHtmlMultiModule by tasks.existing(DokkaMultiModuleTask::class) {
    failOnWarning.set(true)
    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        val olderDokkaVersionsDir: String? by project
        version = liveCallbackVersion
        olderVersionsDir = olderDokkaVersionsDir?.let { projectDir.resolve(it) }
    }
}

val dokkaHtmlMultiModuleVersioning by tasks.registering {
    dependsOn(dokkaHtmlMultiModule)
    doLast {
        val olderDokkaVersionsDir: String? by project
        val docsOutputDir: String by project
        val outputDir = dokkaHtmlMultiModule.map(DokkaMultiModuleTask::outputDirectory)
        val createdVersionDir = "$olderDokkaVersionsDir/$liveCallbackVersion"

        copy {
            from(outputDir)
            into(docsOutputDir)
        }
        copy {
            from(docsOutputDir)
            into(createdVersionDir)
            exclude("older/")
        }
    }
}

val liveCallbackVersion by extra("1.0.0")
val lifecycleVersion by extra("2.4.0")
val junitVersion by extra("4.13.2")
