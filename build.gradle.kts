plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.hibernate:hibernate-core:5.5.6.Final")
    runtimeOnly("org.postgresql:postgresql:42.7.5")
    implementation("com.vladmihalcea:hibernate-types-52:2.12.1")
    implementation("org.slf4j:slf4j-api:2.0.17")

    implementation("org.slf4j:slf4j-log4j12:2.0.16")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    testCompileOnly("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.test {
    useJUnitPlatform()
}