plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}


group = "fr.bibonne"

version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("bibonne.exp.oas-cache:metadata-modelapi:0.0.1-SNAPSHOT")
    implementation("bibonne.exp.oas-cache:metadata-modelapi:0.0.1-SNAPSHOT")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}