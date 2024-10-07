import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestLogging
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI
import java.util.Locale

val snapshot = System.getenv("SNAPSHOT_BUILD").let {
    if (it.isNullOrBlank() || "yes" == it.lowercase(Locale.getDefault())) "-snapshot" else ""
}

version = "%s%s".format(version, snapshot)

kotlin {
    explicitApi()
}

plugins {
    `java-library`
    `maven-publish`
    jacoco
    alias(libs.plugins.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.serialization)
    alias(libs.plugins.sonatype.central.upload)
}

repositories {
    mavenCentral()
}

val gradleTasks by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

configurations {
    compileClasspath {
        extendsFrom(gradleTasks)
    }
}

dependencies {
    // Non-transitive dependencies.
    implementation(libs.caffeine)

    // Transitive dependencies.
    api(libs.percentage)

    // Required only by the project Gradle tasks.
    gradleTasks(libs.icu)
    gradleTasks(libs.ktor.client.cio)
    gradleTasks(libs.ktor.serialization.kotlinx.json)
    gradleTasks(libs.kotlinx.serialization.json)

    // Plugins.
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.libraries)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(8)
    withSourcesJar()
}

detekt {
    config.setFrom("../detekt.yml")
    buildUponDefaultConfig = true

    source.setFrom(
        "src/main/kotlin",
        "src/performanceTest/kotlin",
        "src/test/kotlin"
    )
}

testing {
    suites {
        val performanceTest by registering(JvmTestSuite::class) {
            testType = TestSuiteType.PERFORMANCE_TEST

            targets {
                all {
                    testTask.configure {
                        description = "Runs the performance tests."
                        shouldRunAfter("test")
                    }
                }
            }
        }

        withType(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(libs.kotlin.test.junit5)
                implementation(libs.junit.jupiter.engine)
                runtimeOnly(libs.junit.platform.launcher)
            }

            targets {
                all {
                    testTask.configure {
                        testLogging {
                            exceptionFormat = TestExceptionFormat.FULL
                            events(
                                TestLogEvent.FAILED,
                                TestLogEvent.SKIPPED,
                                TestLogEvent.STANDARD_ERROR,
                                TestLogEvent.STANDARD_OUT,
                            )
                        }
                    }
                }
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")

        // configure<JacocoTaskExtension> {
            // Exclude package from coverage data collection.
            // excludes = listOf("com.eriksencosta.money.gradle.*")

            // Include classes without source in the instrumentation.
            // isIncludeNoLocationClasses = true
        // }
    }

    jacocoTestReport {
        reports.xml.required = true
        sourceSets(sourceSets.main.get())
        dependsOn("test")

        // Exclude directory from generated report.
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it) { exclude("com/eriksencosta/money/gradle/**") }
        }))
    }

    jar {
        base {
            manifest {
                attributes(mapOf(
                    "Implementation-Title" to "${project.group}.${project.name}",
                    "Implementation-Version" to project.version
                ))
            }
        }

        exclude("com/eriksencosta/money/gradle/**")
    }

    sonatypeCentralUpload {
        username = System.getenv("MAVEN_CENTRAL_USERNAME")
        password = System.getenv("MAVEN_CENTRAL_PASSWORD")

        archives = files(*jars())
        pom = file("build/publications/pom/pom-default.xml")

        signingKey = System.getenv("GPG_PRIVATE_KEY")
        signingKeyPassphrase = System.getenv("GPG_PRIVATE_KEY_PASSWORD")
        publicKey = System.getenv("GPG_PUBLIC_KEY")

        publishingType = "MANUAL"

        // Allows to run the plugin without rebuilding the project due to available jar files in
        // its publishing directory.
        doFirst { delete("build/sonatype-central-upload") }

        dependsOn("build")
        mustRunAfter("build")
    }

    build {
        dependsOn("detekt", "test", "generatePomFileForPomPublication", "generateJavadocJar")
    }

    withType<DokkaTask>().configureEach {
        dokkaSourceSets {
            named("main") {
                moduleName = "Money"
                includes.from("dokka.md")

                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(
                        URI("https://github.com/eriksencosta/money/tree/trunk/money/src/main/kotlin").toURL()
                    )
                    remoteLineSuffix.set("#L")
                }

                externalDocumentationLink {
                    url.set(URI("https://blog.eriksen.com.br/opensource/math-common/").toURL())
                    packageListUrl.set(
                        URI("https://blog.eriksen.com.br/opensource/math-common/-math%20-common/package-list").toURL()
                    )
                }

                externalDocumentationLink {
                    url.set(URI("https://blog.eriksen.com.br/opensource/math-percentage/").toURL())
                    packageListUrl.set(
                        URI("https://blog.eriksen.com.br/opensource/math-percentage/-percentage/package-list").toURL()
                    )
                }
            }
        }
    }

    register<Copy>("patchDokkaNavigationJavascript") {
        from(layout.buildDirectory.file("dokka/html/scripts/navigation-loader.js"))
        into(layout.buildDirectory.dir("dokka/html"))

        filter { line ->
            if (line.trim() == "document.querySelectorAll(\".overview > a\").forEach(link => {")
                "document.querySelectorAll(\".overview > a\").forEach((link, index) => {"
            else line
        }

        filter { line ->
            if (line.trim() == "link.setAttribute(\"href\", pathToRoot + link.getAttribute(\"href\"));")
                """
                // The first sidebar navigation link doesn't work properly when the files are hosted on a subdirectory
                // like "example.com/dokka/html".
                link.setAttribute("href",
                    pathToRoot + link.getAttribute("href").replace(/^(\/opensource\/.*?\/)(.*)/, "$2")
                );
                """
            else line
        }

        dependsOn("dokkaHtml")
    }

    register<Copy>("dokkaHtmlPatched") {
        description = "Generates documentation in 'html' format with applied patches."
        group = "Documentation"

        from(layout.buildDirectory.file("dokka/html/navigation-loader.js"))
        into(layout.buildDirectory.dir("dokka/html/scripts"))

        dependsOn("patchDokkaNavigationJavascript")
    }

    register<Jar>("generateJavadocJar") {
        description = "Generates the Javadoc jar."
        group = "Documentation"

        from(dokkaJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")

        dependsOn("dokkaJavadoc")
        mustRunAfter("sourcesJar")
    }

    register<Task>("generateDocs") {
        description = "Generates the project documentation (HTML, Markdown, and Javadoc)."
        group = "Documentation"

        dependsOn("dokkaHtmlPatched", "dokkaGfm", "dokkaJavadoc")
    }

    register<Task>("release") {
        description = "Builds and publishes the library to Maven Central."
        group = "Release"

        dependsOn("build", "sonatypeCentralUpload", "version")
        mustRunAfter("sonatypeCentralUpload", "version")
    }

    register<Task>("version") {
        description = "Prints the version stamp."
        group = "Verification"

        doLast {
            logger.lifecycle("${project.group}:${project.name}:$version")
        }
    }

    register<JavaExec>("generateCirculatingCurrenciesDataClasses") {
        description = "Generates the circulating currencies data classes."
        group = "Internal dataset"

        classpath = runtimeClasspath()
        mainClass = "com.eriksencosta.money.gradle.CirculatingCurrenciesDataClassesGenerator"

        args(*sourceDirectories())
    }

    register<JavaExec>("generateCryptoCurrenciesDataClasses") {
        description = "Generates the cryptourrencies data classes."
        group = "Internal dataset"

        classpath = runtimeClasspath()
        mainClass = "com.eriksencosta.money.gradle.CryptoCurrenciesDataClassesGenerator"

        args(*sourceDirectories())
    }

    register<Task>("generateCurrencyDataClasses") {
        description = "Generates the currencies data classes."
        group = "Internal dataset"

        dependsOn("generateCirculatingCurrenciesDataClasses", "generateCryptoCurrenciesDataClasses")
    }

    register<JavaExec>("generateCirculatingCurrenciesDocumentation") {
        description = "Generates the circulating currencies documentation."
        group = "Documentation"

        classpath = runtimeClasspath()
        mainClass = "com.eriksencosta.money.gradle.CirculatingCurrenciesDocumentationGenerator"

        args(documentationDirectory())
    }

    register<JavaExec>("generateCryptoCurrenciesDocumentation") {
        description = "Generates the cryptocurrencies documentation."
        group = "Documentation"

        classpath = runtimeClasspath()
        mainClass = "com.eriksencosta.money.gradle.CryptoCurrenciesDocumentationGenerator"

        args(documentationDirectory())
    }

    register<Task>("generateCurrenciesDocumentation") {
        description = "Generates the currencies documentation (appendixes)."
        group = "Documentation"

        dependsOn("generateCirculatingCurrenciesDocumentation", "generateCryptoCurrenciesDocumentation")
    }
}

publishing {
    publications {
        create<MavenPublication>("pom") {
            pom {
                description = "Monetary calculations and allocations made easy"
                url = "https://github.com/eriksencosta/money"

                developers {
                    developer {
                        name = "Eriksen Costa"
                        url = "https://blog.eriksen.com.br"
                    }
                }

                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }

                scm {
                    url = "git@github.com:eriksencosta/money.git"
                    tag = "trunk"
                }

                issueManagement {
                    system = "GitHub"
                    url = "https://github.com/eriksencosta/money/issues"
                }

                ciManagement {
                    system = "GitHub Actions"
                    url = "https://github.com/eriksencosta/money/actions"
                }

                // Dependencies
                from(components["java"])
            }
        }
    }
}

private fun jars() = arrayOf(jarName(), jarName("javadoc"), jarName("sources"))

private fun jarName(kind: String = "") = "build/libs/$name-%s%s.jar"
    .format(version, if (kind.isNotBlank()) "-$kind" else "")

private fun runtimeClasspath() = sourceSets.main.get().runtimeClasspath

private fun sourceDirectories() = listOf(
    sourceSets.main.get().kotlin.sourceDirectories.files.find { it.name.endsWith("kotlin") },
    sourceSets.test.get().kotlin.sourceDirectories.files.find { it.name.endsWith("kotlin") }
).map { "$it/com/eriksencosta/money" }.toTypedArray()

private fun documentationDirectory() = layout.projectDirectory.dir("../docs/appendixes")
