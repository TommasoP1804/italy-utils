plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("maven-publish")
    id("io.freefair.aspectj.post-compile-weaving") version "9.1.0" // AspectJ plugin
    id("com.vanniktech.maven.publish") version "0.30.0"
    signing
}

group = "com.sigeosrl"
version = "2026-02.1"
// © S.I.GEO s.r.l. | Italy-Utils
// Referent: Tommaso Pastorelli
// Last update: Tommaso Pastorelli | 20260207T004515Z

repositories {
    mavenCentral()
    maven { url = uri("https://repo.osgeo.org/repository/release/") }
    maven { url = uri("https://download.osgeo.org/webdav/geotools/") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.locationtech.jts.io:jts-io-common:1.19.0")
    implementation("org.hibernate:hibernate-spatial:6.6.0.Final")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.github.lalyos:jfiglet:0.0.8")
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation(kotlin("scripting-jsr223"))
    implementation("org.openjdk.nashorn:nashorn-core:15.3")
    implementation("org.aspectj:aspectjrt:1.9.24")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.slf4j:jul-to-slf4j:2.0.13")
    implementation("org.aspectj:aspectjrt:1.9.24")
    implementation("org.aspectj:aspectjweaver:1.9.24")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:jul-to-slf4j:2.0.13")
    implementation("dev.tommasop1804:kotlin-utils:1.0.0")
    implementation("tools.jackson.core:jackson-databind:3.0.2")
    implementation("tools.jackson.core:jackson-core:3.0.2")
    implementation("tools.jackson.module:jackson-module-kotlin:2.20.1")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xallow-contracts-on-more-functions")
        freeCompilerArgs.add("-Xcontext-parameters")
        /*freeCompilerArgs.add("-Xallow-condition-implies-returns-contracts")
        freeCompilerArgs.add("-Xallow-holdsin-contract")*/

    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.compilerArgs.plusAssign("-Xlint:adviceDidNotMatch=ignore")
}

mavenPublishing {
    coordinates("dev.tommasop1804", "italy-utils", "2026-02")

    pom {
        name.set("Italy Utils")
        description.set("Utility functions and classes for Italy (Kotlin and Java)")
        inceptionYear.set("2024")
        url.set("https://github.com/TommasoP1804")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("tommasop1804")
                name.set("Tommaso")
                url.set("https://tommasop1804.dev")
            }
        }
        scm {
            url.set("https://github.com/tommasop1804/italy-utils")
            connection.set("scm:git:git://github.com/tommasop1804/italy-utils.git")
            developerConnection.set("scm:git:ssh://git@github.com/tommasop1804/italy-utils.git")
        }
    }

    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}