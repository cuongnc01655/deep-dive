plugins {
    id("java")
    alias(libs.plugins.jmh)
}

group = "me"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.agrona)
    implementation(libs.slf4j)
    implementation(libs.logback)
    implementation(libs.jmhcore)
    testImplementation(libs.bundles.testing)
    annotationProcessor(libs.jmhannprocess)
}

tasks.test {
    useJUnitPlatform()
}

jmh {
    jmhVersion.set(libs.versions.jmhVersion.get())
    verbosity.set("EXTRA")
}
