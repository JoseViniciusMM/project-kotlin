package br.com.filacidada

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.github.serpro69.kfaker.Faker
import org.bson.Document
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.util.Date
import java.util.UUID

// Quantidade de registros fake a gerar
const val FAKE_USUARIOS_COUNT = 20

fun main() {
    val mongoUri = System.getenv("MONGO_URI") ?: "mongodb://localhost:27017/"
    val dbName = System.getenv("MONGO_DB") ?: "FilaCidada"

    val faker = Faker()

    println("Conectando ao MongoDB: $dbName ...")
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(mongoUri))
        .build()
    val client = MongoClients.create(settings)
    val db: MongoDatabase = client.getDatabase(dbName)

    val collections = listOf("usuarios")
    collections.forEach { col ->
        db.getCollection(col).drop()
        println("  Coleção '$col' limpa.")
    }

    val now = Date.from(Instant.now())
    fun hash(plain: String): String = BCrypt.hashpw(plain, BCrypt.gensalt(12))

    val papeis = listOf("ADMIN_PLATAFORMA", "ADMIN_INSTITUICAO", "ATENDENTE", "USUARIO_FINAL")

    // 1. USUÁRIOS FIXOS (Para você testar o login no Postman/Insomnia)
    val usuarios = db.getCollection("usuarios")
    val fixedUsuarios = listOf(
        Document(mapOf(
            "_id" to "usr-admin-plataforma",
            "nome" to "Admin Plataforma",
            "email" to "admin@example.com",
            "senhaHash" to hash("Admin@123"),
            "papeis" to listOf("ADMIN_PLATAFORMA"),
            "ativo" to true,
            "fusoHorario" to "America/Manaus",
            "createdAt" to now
        )),
        Document(mapOf(
            "_id" to "usr-admin-instituicao",
            "nome" to "Admin Instituicao",
            "email" to "admin.instituicao@example.com",
            "senhaHash" to hash("Admin@123"),
            "papeis" to listOf("ADMIN_INSTITUICAO"),
            "instituicaoId" to "inst-exemplo",
            "ativo" to true,
            "fusoHorario" to "America/Manaus",
            "createdAt" to now
        )),
        Document(mapOf(
            "_id" to "usr-atendente",
            "nome" to "Atendente de Guichê",
            "email" to "atendente@example.com",
            "senhaHash" to hash("Admin@123"),
            "papeis" to listOf("ATENDENTE"),
            "instituicaoId" to "inst-exemplo",
            "ativo" to true,
            "fusoHorario" to "America/Manaus",
            "createdAt" to now
        )),
        Document(mapOf(
            "_id" to "usr-cidadao",
            "nome" to "Cidadão José",
            "email" to "cidadao@example.com",
            "senhaHash" to hash("Admin@123"),
            "papeis" to listOf("USUARIO_FINAL"),
            "ativo" to true,
            "fusoHorario" to "America/Manaus",
            "createdAt" to now
        ))
    )

    // 2. USUÁRIOS FAKE
    val fakeUsuarios = (1..FAKE_USUARIOS_COUNT).map { i ->
        val nome = faker.name.name()
        val email = faker.internet.email()
        val papel = papeis[i % papeis.size]
        val instId = if (papel in listOf("ADMIN_INSTITUICAO", "ATENDENTE"))
            "inst-${faker.lorem.words()}" else null

        val doc = mutableMapOf<String, Any?>(
            "_id" to "usr-fake-$i-${UUID.randomUUID().toString().take(8)}",
            "nome" to nome,
            "email" to email,
            "senhaHash" to hash("Fake@${i}23"),
            "papeis" to listOf(papel),
            "ativo" to faker.random.nextBoolean(),
            "fusoHorario" to "America/Manaus",
            "createdAt" to now
        )
        if (instId != null) doc["instituicaoId"] = instId
        Document(doc)
    }

    usuarios.insertMany(fixedUsuarios + fakeUsuarios)

    println()
    println("---------------------------------------")
    println("Seed concluído com sucesso!")
    println("---------------------------------------")
    println("  Usuários inseridos: ${usuarios.countDocuments()}")
    println("---------------------------------------")
    println("Credenciais de acesso para testar na API:")
    println("  ADMIN_PLATAFORMA:  admin@example.com              / Admin@123")
    println("  ADMIN_INSTITUICAO: admin.instituicao@example.com  / Admin@123")
    println("  ATENDENTE:         atendente@example.com          / Admin@123")
    println("  USUARIO_FINAL:     cidadao@example.com            / Admin@123")
    println()

    client.close()
}