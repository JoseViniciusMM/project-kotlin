package br.com.filacidada.repositories
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Updates
import org.litote.kmongo.*
import br.com.filacidada.models.*


class SenhaRepositoryImpl(
    private val collection: MongoCollection<Senha>
) : SenhaRepository {

    override fun findById(id: String): Senha? {
        return collection.findOneById(id)
    }

    override fun findAll(page: Int, limit: Int, filters: Map<String, Any?>): Pair<List<Senha>, Long> {
        val bsonFilters = buildFilters(filters)
        val query = if (bsonFilters.isNotEmpty()) and(bsonFilters) else "{}"

        // org.bson.conversions.Bson já é resolvido nativamente pelo KMongo se o import estiver certo
        val total = collection.countDocuments(query as org.bson.conversions.Bson)

        val docs = collection.find(query)
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()

        return Pair(docs, total)
    }

    override fun insert(senha: Senha): Senha {
        collection.insertOne(senha)
        return senha
    }

    override fun update(id: String, updates: Map<String, Any?>): Boolean {
        val setUpdates = updates.map { (key, value) -> Updates.set(key, value) }
        // Adicionado o 'Updates.' antes do combine para ele achar a função do MongoDB
        val result = collection.updateOneById(id, Updates.combine(setUpdates))
        return result.modifiedCount > 0
    }

    override fun hasSenhaAtivaNaFila(usuarioId: String, filaId: String): Boolean {
        val query = and(
            Senha::usuarioId eq usuarioId,
            Senha::filaId eq filaId,
            Senha::status `in` listOf(StatusSenha.AGUARDANDO, StatusSenha.EM_ATENDIMENTO)
        )
        return collection.countDocuments(query) > 0
    }

    override fun getUltimaSenhaDaFila(filaId: String): Int {
        val ultima = collection.find(Senha::filaId eq filaId)
            .sort(descending(Senha::posicao))
            .limit(1)
            .firstOrNull()

        return ultima?.posicao ?: 0
    }

    override fun countByFilaIdAndStatus(filaId: String, status: StatusSenha): Long {
        val query = and(Senha::filaId eq filaId, Senha::status eq status)
        return collection.countDocuments(query)
    }

    private fun buildFilters(filters: Map<String, Any?>): List<org.bson.conversions.Bson> {
        return filters.map { (key, value) ->
            com.mongodb.client.model.Filters.eq(key, value)
        }
    }


}