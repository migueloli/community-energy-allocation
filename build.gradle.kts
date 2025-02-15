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
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(platform(SpringBootPlugin.BOM_COORDINATES))

	// Spring Boot + MongoDB
	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.boot.starter.data.mongodb)

	// Spring Security
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.validation)
	implementation(libs.spring.boot.starter.oauth2.resource.server)
	implementation(libs.spring.boot.starter.oauth2.client)
	implementation(libs.spring.boot.starter.actuator)

	// JWT
	implementation(libs.jjwt.api)
	runtimeOnly(libs.jjwt.impl)
	runtimeOnly(libs.jjwt.jackson)

	// DB Driver
	implementation(libs.mongodb.driver)

	// CSV parsing
	implementation(libs.apache.commons.csv)

	// Lombok
	implementation(libs.lombok)

	// MapStruct
	implementation(libs.mapstruct)
	annotationProcessor(libs.mapstruct.processor)

	// Swagger UI
	implementation(libs.springdoc.openapi.starter.webmvc.ui)

	// Testing
	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.spring.security.test)
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.mockito.core)
}

val mockitoAgent = configurations.create("mockitoAgent")
tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs(
		"-javaagent:${mockitoAgent.asPath}",
		"-Xshare:off"
	)
	systemProperty("spring.profiles.active", "test")
}