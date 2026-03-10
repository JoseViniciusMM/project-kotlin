import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Sorts
import org.litote.kmongo.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant

class SenhaRepositoryImpl ( 
    private val collection: MongoCollection<Senha>
) : SenhaRepository {

    override fun findById(id: String): Senha? {
        return collection.findOneById(id)
    }

    override fun findAll(page: Int, limit: Int, filters: Map<String, Any?>): Pair<List<Senha>, Long> {
        val bsonFilters = buildFilters(filters)
        val query = if (bsonFilters.isNotEmpty()) and(bsonFilters) else "{}"
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
        val result = collection.updateOneById(id, combine(setUpdates))
        return result.modifiedCount > 0
    }

    override fun hasSenhaAtivaNaFila() {
        //falta implementar /nao sei
    }

    override fun getUltimaSenhaDaFila() {
        //falta implementar /nao sei
    }

    private fun buildFilters () {
        //falta fazer o build dos filtros
    }
}