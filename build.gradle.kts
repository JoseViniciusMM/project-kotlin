// 1. Alinhando as versões para a 3.0.1 (Estável e compatível com Gradle 8.5+)
val ktor_version = "3.0.1"
val kotlin_version = "1.9.22"
val kmongo_version = "4.11.0"
val koin_version = "3.5.3"
val logback_version = "1.4.14"

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    // O plugin deve acompanhar a versão das bibliotecas
    id("io.ktor.plugin") version "3.0.1"
    application
}

group = "br.com.filacidada"
version = "3.0.0"

application {
    mainClass.set("br.com.filacidada.ApplicationKt") // Adicionado o pacote completo (boa prática)
}

repositories {
    mavenCentral()
}

dependencies {
    // ── Ktor Server ──
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")

    // ── Serialização ──
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // ── MongoDB (KMongo) ──
    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-serialization:$kmongo_version")

    // ── Koin (DI) ──
    // Nota: Para Ktor 3.x, verifique se o koin-ktor está atualizado
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")

    // ── JWT & BCrypt ──
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.mindrot:jbcrypt:0.4")

    // ── Swagger / OpenAPI ──
    implementation("io.ktor:ktor-server-openapi:$ktor_version")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")

    // ── E-mail ──
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // ══════════════ TESTES ══════════════
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("io.insert-koin:koin-test:$koin_version")
    testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:mongodb:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("seed") {
    description = "Popula o banco MongoDB com dados de exemplo"
    group = "application"
    mainClass.set("br.com.filacidada.DatabaseSeedKt")
    classpath = sourceSets["main"].runtimeClasspath
}

kotlin {
    // Como configuramos o JAVA_HOME para o JDK 21, recomendo usar 21 aqui também
    // Mas se quiser manter 17 para compatibilidade de servidor, funciona igual.
    jvmToolchain(17)
}