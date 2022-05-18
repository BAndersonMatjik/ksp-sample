plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}


kotlin.sourceSets.main {
    kotlin.srcDirs(
        file("$buildDir/generated/ksp/main/kotlin"),
    )
}
dependencies {
    implementation(project(":annotations"))
    ksp(project(":processor"))
}