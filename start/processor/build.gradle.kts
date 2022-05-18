plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    `maven-publish`
    signing
}

// Versions are declared in 'gradle.properties' file
val kspVersion: String by project
java {
    withJavadocJar()
    withSourcesJar()
}

ksp {
    arg("autoserviceKsp.verify", "true")
    arg("autoserviceKsp.verbose", "true")
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = "com.bmatjik.genfieldtolist.processor"
//            artifactId = "library"
//            version = "1.0.0"
//
//            from(components["java"])
//        }
//    }
//}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "com.bmatjik.genfieldtolist.processor"
            version = "1.0.0"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Gen Field To List")
                description.set("Generate Data Class Member to String ")
                url.set("http://www.example.com/library")
                properties.set(
                    mapOf(
                        "myProp" to "value",
                        "prop.with.dots" to "anotherValue"
                    )
                )

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("daisukikancolle ")
                        name.set("Billy Anderson Matjik")
                        email.set("daisukikancolle@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/BAndersonMatjik/ksp-sample")
                    developerConnection.set("scm:git:ssh://github.com/BAndersonMatjik/ksp-sample.git")
                    url.set("https://github.com/BAndersonMatjik/ksp-sample")
                }
            }
        }
    }

//    repositories {
//        maven {
//            val url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
//            credentials {
//                username = findProperty("sonatypeUsername")
//                password = findProperty("sonatypePassword")
//            }
//        }
//    }
    repositories {

        maven(url = "https://pkgs.dev.azure.com/daisukikancolle0492/_packaging/daisukikancolle0492/maven/v1", action = {
            name = "daisukikancolle0492"
            credentials {
                username = "test"
                password = "6nqepvsoztisw64fyhsvigjj3fo6jc4prdeixoxp4ihcut5gf5aa"
            }
        })
    }
}

signing {
    useGpgCmd()
    sign(configurations.archives.get())
}

dependencies {
    implementation(project(":annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service-annotations
    implementation("com.google.auto.service:auto-service-annotations:1.0")
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.0.0")

    implementation("com.squareup:kotlinpoet:1.10.1")
    implementation("com.squareup:kotlinpoet-ksp:1.10.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.4.32")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.4")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.4")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
}