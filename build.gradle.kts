import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.22"
    val springBootVersion = "3.2.3"
    val springDepMgmtVersion = "1.1.4"
    val versionsVersion = "0.51.0"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version springDepMgmtVersion
    id("com.github.ben-manes.versions") version versionsVersion // ./gradlew dependencyUpdates to check for new versions
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    maven("https://maven.pkg.github.com/navikt/navno-search-admin-api") {
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: "x-access-token"
            password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("githubPassword") as String
        }
    }
    mavenCentral()
}

dependencies {
    val logstashVersion = "7.4"
    val opensearchVersion = "1.3.0"
    val opensearchTestcontainersVersion = "2.0.1"
    val testcontainersVersion = "1.19.6"
    val navnoSearchCommonVersion = "20240221092424-e37515a"

    implementation("no.nav.navnosearchadminapi:common:$navnoSearchCommonVersion")
    implementation("org.opensearch.client:spring-data-opensearch-starter:$opensearchVersion") {
        exclude("org.opensearch.client", "opensearch-rest-client-sniffer")
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.opensearch.client:spring-data-opensearch-test-autoconfigure:$opensearchVersion") {
        exclude("org.opensearch.client", "opensearch-rest-client-sniffer")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.opensearch:opensearch-testcontainers:$opensearchTestcontainersVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
}
