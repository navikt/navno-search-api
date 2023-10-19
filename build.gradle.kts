import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val versions = object {
        val kotlin = "1.9.0"
        val springBoot = "3.1.4"
        val springDepMgmt = "1.1.3"
        val versions = "0.49.0"
    }

    kotlin("jvm") version (versions.kotlin)
    kotlin("plugin.spring") version (versions.kotlin)

    id("org.springframework.boot") version (versions.springBoot)
    id("io.spring.dependency-management") version (versions.springDepMgmt)
    id("com.github.ben-manes.versions") version (versions.versions) // ./gradlew dependencyUpdates to check for new versions
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    val versions = object {
        val coroutines = "1.7.3"
        val logstash = "7.4"
        val opensearch = "1.2.0"
        val opensearchTestcontainers = "2.0.0"
        val testcontainers = "1.18.3"
    }

    implementation("org.opensearch.client:spring-data-opensearch-starter:${versions.opensearch}") {
        exclude("org.opensearch.client", "opensearch-rest-client-sniffer")
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("net.logstash.logback:logstash-logback-encoder:${versions.logstash}")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.opensearch.client:spring-data-opensearch-test-autoconfigure:${versions.opensearch}") {
        exclude("org.opensearch.client", "opensearch-rest-client-sniffer")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.0.4")
    testImplementation("org.testcontainers:junit-jupiter:${versions.testcontainers}")
    testImplementation("org.opensearch:opensearch-testcontainers:${versions.opensearchTestcontainers}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
}
