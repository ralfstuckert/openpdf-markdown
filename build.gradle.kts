import java.net.URI

plugins {
    kotlin("jvm") version "2.2.10"
    `maven-publish`
    jacoco
}

group = "com.github.ralfstuckert"
version = "0.1.1-SNAPSHOT"

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/ralfstuckert/pdftools")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER") ?: ""
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }

    mavenCentral()
}



dependencies {
    implementation("org.jetbrains:markdown:0.7.3")
    implementation("com.github.librepdf:openpdf:2.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
    testImplementation("com.github.ralfstuckert:pdftools:0.4.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

val jacocoExclude =
    listOf("**/Main.kt")

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
    }
    classDirectories.setFrom(
        classDirectories.files.map {
            fileTree(it).matching {
                exclude(jacocoExclude)
            }
        },
    )
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = rootProject.name
            version = version

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/ralfstuckert/openpdf-markdown")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}