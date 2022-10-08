import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("org.springframework.boot") version "2.7.4"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
    id("io.gitlab.arturbosch.detekt") version "1.22.0-RC1"
    id("org.jetbrains.kotlinx.kover") version "0.4.2"

    kotlin("jvm") version "1.7.20"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

group = "com.productboard"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda")
    implementation("com.vladmihalcea:hibernate-types-52:2.19.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.0")

    implementation("org.liquibase:liquibase-core")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("com.h2database:h2:2.1.214")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2:2.1.214")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0-RC1")
}

springBoot {
    buildInfo()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    /* create Docker images with tags "latest" */
    imageName = project.name
    /* also create with a tag of the current application version */
    tags = listOf("${project.name}:$version")
    environment = mapOf(
        /* options for remote application debugging */
        "JAVA_TOOL_OPTIONS" to "-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n"
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

detekt {
    // Version of Detekt that will be used. When unspecified the latest detekt
    // version found will be used. Override to stay on the same version.
    toolVersion = "1.22.0-RC1"

    // The directories where detekt looks for source files.
    // Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
    source = files("src/main/java", "src/main/kotlin")

    // Builds the AST in parallel. Rules are always executed in parallel.
    // Can lead to speedups in larger projects.
    parallel = false

    // Define the detekt configuration(s) you want to use.
    // Defaults to the default detekt configuration.
    config = files("detekt/config.yml")

    // Applies the config files on top of detekt's default config file.
    buildUponDefaultConfig = true

    // Turns on all the rules.
    allRules = false

    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    baseline = file("detekt/baseline.xml")

    // Disables all default detekt rulesets and will only run detekt with custom rules
    // defined in plugins passed in with `detektPlugins` configuration.
    disableDefaultRuleSets = false

    // Adds debug output during task execution.
    debug = false

    // If set to `true` the build does not fail when the
    // maxIssues count was reached. Defaults to `false`.
    ignoreFailures = false

    // Android: Don't create tasks for the specified build types (e.g. "release")
    ignoredBuildTypes = listOf("release")

    // Android: Don't create tasks for the specified build flavor (e.g. "production")
    ignoredFlavors = listOf("production")

    // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
    ignoredVariants = listOf("productionRelease")

    // Specify the base path for file paths in the formatted reports.
    // If not set, all file paths reported will be absolute file path.
    // basePath = projectDir
}

tasks.withType<Detekt>().configureEach {
    reports {
        // Enable/Disable XML report
        xml.required.set(true)
        xml.outputLocation.set(file("build/reports/detekt.xml"))
        // Enable/Disable HTML report
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
        // Enable/Disable TXT report
        txt.required.set(true)
        txt.outputLocation.set(file("build/reports/detekt.txt"))
        // Enable/Disable SARIF report
        sarif.required.set(false)
        sarif.outputLocation.set(file("build/reports/detekt.sarif"))
        // Enable/Disable MD report
        md.required.set(true)
        md.outputLocation.set(file("build/reports/detekt.md"))
        custom {
            // The simple class name of your custom report.
            reportId = "CustomJsonReport"
            outputLocation.set(file("build/reports/detekt.json"))
        }
    }
}
