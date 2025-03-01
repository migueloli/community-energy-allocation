import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.ilo.energy"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.AMAZON
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))

    // Spring Boot
    implementation(libs.bundles.spring.boot.starter)

    // JWT
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // Data
    implementation(libs.opencsv)

    // Lombok
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // MapStruct
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // Resilience4j
    implementation(libs.bundles.resilience4j)

    // Bucket4j
    implementation(libs.bundles.bucket4j)

    // Mongock
    implementation(libs.bundles.mongock)

    // Swagger UI
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // Testing
    testImplementation(libs.bundles.spring.boot.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs(
        "-javaagent:${mockitoAgent.asPath}",
        "-Xshare:off"
    )
    systemProperty("spring.profiles.active", "test")
}