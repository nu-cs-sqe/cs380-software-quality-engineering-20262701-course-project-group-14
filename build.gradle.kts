plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    jacoco
    id("info.solidsoft.pitest") version "1.15.0"
}

group = "nu.csse.sqe"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

javafx {
    version = "17.0.10"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("ui.MainApp")
}

tasks.compileJava {
    options.release = 11
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}

pitest {
    pitestVersion.set("1.15.8")
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(setOf("domain.*", "controller.*"))
    targetTests.set(setOf("domain.*", "controller.*", "integration.*", "ui.*"))
    excludedClasses.set(setOf(
        "domain.CardType", "domain.TurnPhase", "domain.DrawStatus",
        "domain.PlayStatus", "domain.PlayFailure", "domain.DefuseResult",
        "domain.AdvanceResult", "domain.ExplosionOutcome"
    ))
    threads.set(2)
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}