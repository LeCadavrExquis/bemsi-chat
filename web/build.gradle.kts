plugins {
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("java")
}

group = "pl.pw"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
//    implementation("org.springframework.boot:spring-boot-starter-reactor-netty")
//    implementation("org.springframework:spring-messaging")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    compileOnly ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
