plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.howandev.velocitystats"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform:papermc"))
    implementation(project(":platform:velocity"))
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveBaseName.set("VelocityStats")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}
