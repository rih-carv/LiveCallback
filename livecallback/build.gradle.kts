plugins {
    id("java-library")
    id("org.jetbrains.dokka") version "1.5.0"
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(embeddedKotlin("stdlib"))
    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.1")

    testImplementation("junit:junit:4.13.2")
}