plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.2"
}


group = "bibonne.exp"

version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_22
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

graalvmNative {
    binaries {
        all {
            jvmArgs("--enable-preview")
        }
   }
}


tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test>().configureEach {
    jvmArgs("--enable-preview")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-preview")
}



repositories {
    mavenCentral()
    maven{
        url = uri("https://maven.pkg.github.com/FBibonne/Properties-Logger")
        credentials{
            // set the property github.username in $GRADLE_HOME/gradle.properties file like this `github.username='FBibonne'`
            username = providers.gradleProperty("github.username").toString()
            // set the property github.user_token in $GRADLE_HOME/gradle.properties file like this `github.user_token='ghp_****'`
            password = providers.gradleProperty("github.user_token").toString()
        }
    }
    mavenLocal()
}

dependencies {
    compileOnly("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("fr.insee:boot-properties-logger-starter:1.0.0-SNAPSHOT")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
