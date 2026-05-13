package br.com.filacidada.config

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

import br.com.filacidada.models.*
import br.com.filacidada.utils.Constants

class MongoConfig(
    connectionString: String = System.getenv("MONGO_URI") ?: "mongodb+srv://fs_aula:60egYQtIbESPneFx@cluster0.bpx8s93.mongodb.net/Aula",
    databaseName: String = System.getenv("MONGO_DB") ?: "Aula"
) {
    private val client = KMongo.createClient(connectionString)
    val database: MongoDatabase = client.getDatabase(databaseName)

    val usuarios get() = database.getCollection<Usuario>(Constants.COLLECTION_USUARIOS)
    val auditorias get() = database.getCollection<Auditoria>("auditorias")
    val filas get() = database.getCollection<Fila>("filas")
    val instituicoes get() = database.getCollection<Instituicao>("instituicoes")
    val landingPages get() = database.getCollection<LandingPage>("landing_pages")
    val qrCodes get() = database.getCollection<QrCode>("qr_codes")
    val senhas get() = database.getCollection<Senha>("senhas")
}