plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

// Versions are declared in 'gradle.properties' file
val kspVersion: String by project


ksp {
    arg("autoserviceKsp.verify", "true")
    arg("autoserviceKsp.verbose", "true")
}

dependencies {
    implementation(project(":annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service-annotations
    implementation("com.google.auto.service:auto-service-annotations:1.0")
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.0.0")

    implementation("com.squareup:kotlinpoet:1.10.1")
    implementation("com.squareup:kotlinpoet-ksp:1.10.1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
}