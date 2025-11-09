plugins {
    `java-library`
}

group = "org.smarthome"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

val springBootVersion = "3.4.5"

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
