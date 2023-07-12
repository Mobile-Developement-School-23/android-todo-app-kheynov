// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt") version ("1.23.0")
}

detekt {
    toolVersion = "1.23.0"
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        md.required.set(true)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
