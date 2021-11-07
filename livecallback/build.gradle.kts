plugins {
    kotlin("jvm")
    id("java-library")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

dependencies {
    val lifecycleVersion: String by rootProject.extra
    val junitVersion: String by rootProject.extra

    implementation(embeddedKotlin("stdlib"))
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    testImplementation("junit:junit:$junitVersion")
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadocPartial)
    from(tasks.dokkaJavadocPartial.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val dokkaHtmlJar by tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtmlPartial)
    from(tasks.dokkaHtmlPartial.flatMap { it.outputDirectory })
    archiveClassifier.set("html-doc")
}

publishing {
    val liveCallbackVersion: String by rootProject.extra

    publications.register<MavenPublication>("library") {
        from(components["java"])
        version = liveCallbackVersion
        groupId = "io.github.rih-carv.livecallback"
        artifactId = "livecallback"
        artifact(dokkaJavadocJar)
        artifact(dokkaHtmlJar)
        pom {
            name.set("LiveCallback")
            description.set(
                "LiveCallback is a library project that aims to make dealing with async callbacks" +
                    " on Android safer and straightforward."
            )
            url.set("https://rih-carv.github.io/LiveCallback")
            licenses {
                license {
                    name.set("The MIT License")
                    url.set("https://opensource.org/licenses/mit-license.php")
                }
            }
            developers {
                developer {
                    id.set("rih-carv")
                    name.set("Ricardo de Carvalho")
                    email.set("rih.carv@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/rih-carv/LiveCallback.git")
                developerConnection.set("scm:git:ssh://github.com/rih-carv/LiveCallback.git")
                url.set("https://github.com/rih-carv/LiveCallback/")
            }
        }
    }
    repositories.maven {
        val releases = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        val snapshots = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        url = if (version.toString().endsWith("SNAPSHOT")) snapshots else releases
        name = "SonatypeCentral"

        credentials {
            val sonatypeUsername: String? by project
            val sonatypePassword: String? by project
            username = sonatypeUsername
            password = sonatypePassword
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["library"])
}
