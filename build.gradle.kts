plugins {
    java
    application
}

group = "edu.snykyforov"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.inject:guice:5.1.0")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.xerial:sqlite-jdbc:3.36.0.2")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.22.0")

    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
}

tasks.compileJava {
    options.encoding = "utf-8"
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("banking.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
