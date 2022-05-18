plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "com.bmatjik.genfieldtolist.annotations"
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
                properties.set(mapOf(
                    "myProp" to "value",
                    "prop.with.dots" to "anotherValue"
                ))

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
}

signing {
    useGpgCmd()
    sign(configurations.archives.get())
}