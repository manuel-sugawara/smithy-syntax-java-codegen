plugins {
    `java-library`
    checkstyle
    //id("com.github.spotbugs") version "4.7.3"
    `maven-publish`
}

// The root project doesn't produce a JAR.
tasks["jar"].enabled = false

repositories {
    mavenLocal()
    mavenCentral()
}

// SUBPROJECT SETTINGS
subprojects {
    val subproject = this

    // Set up main java project
    if (subproject.name != "codegen-test") {
        apply(plugin = "java-library")

        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        // Use Junit5's test runner.
        tasks.withType<Test> {
            useJUnitPlatform()
        }

        // Apply junit 5 and hamcrest test dependencies to all java projects.
        dependencies {
            testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
            testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.0")
            testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.0")
            testImplementation("org.hamcrest:hamcrest:2.1")
        }

        // Configure jars to include license related info
        tasks.jar {
            inputs.property("moduleName", subproject.extra["moduleName"])
            manifest {
                attributes["Automatic-Module-Name"] = subproject.extra["moduleName"]
            }
        }

        /*
         * Spotbugs
         * ====================================================

        apply(plugin = "com.github.spotbugs")

        // We don't need to lint tests.
        tasks["spotbugsTest"].enabled = false

        // Configure the bug filter for spotbugs.
        spotbugs {
            // setEffort("max")
            val excludeFile = File("${project.rootDir}/config/spotbugs/filter.xml")
            if (excludeFile.exists()) {
                excludeFilter.set(excludeFile)
            }

        }
        tasks.spotbugsMain {
            reports.create("html") {
                required.set(true)
                outputLocation.set(file("$buildDir/reports/spotbugs.html"))
                setStylesheet("fancy-hist.xsl")
            }
            reports.create("xml") {
                required.set(true)
                outputLocation.set(file("$buildDir/reports/spotbugs.xml"))
            }
        }
	         */
        repositories {
            mavenLocal()
            mavenCentral()
        }
    }
}
