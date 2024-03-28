plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.github.ralfstuckert"
version = "1.0-SNAPSHOT"

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
    implementation("org.jetbrains:markdown:0.5.0")
    implementation("com.github.librepdf:openpdf:1.3.34")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.0")
    testImplementation("com.github.ralfstuckert:pdftools:0.3.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}