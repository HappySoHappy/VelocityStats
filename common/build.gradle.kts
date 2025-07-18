plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // Common dependencies if any
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly("org.projectlombok:lombok:1.18.32")

    implementation("org.spongepowered:configurate-yaml:4.2.0")
}

tasks.jar {
    enabled = true
}
