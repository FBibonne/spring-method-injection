plugins {
    java
}


group = "fr.bibonne"

version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-core:6.1.5")
    implementation("org.springframework:spring-context:6.1.5")
}