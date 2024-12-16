plugins {
    id("java")
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20240303")
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")

}

application {
    // Define the main class for the application.
    mainClass.set("com.aneesh.Main")
}

tasks.named<JavaExec>("run") {
//    Connect the System.in of the gradle build with the System.in of the run task
    standardInput = System.`in`
}



tasks.test {
    useJUnitPlatform()
}