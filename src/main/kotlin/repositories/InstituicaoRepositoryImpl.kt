import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Updates
import org.litote.kmongo.*

class InstituicaoRepositoryImpl( 
    private val collection: MongoCollection<Instituicao>
) : InstituicoesRepository {
     
    override fun findById(id: String): Instituicao? {
        return collection.findOneById(id)
    }

    override fun findAll(page: Int, limit: Int, filters: Map<String, Any?>): Pair<List<Instituicao>, Long> {
        val bsonFilters = buildFilters(filters)
        val query = if (bsonFilters.isNotEmpty()) and(bsonFilters) else "{}"
        val total = collection.countDocuments(query as org.bson.conversions.Bson)
        val docs = collection.find(query)
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
            
        return Pair(docs, total)
    }

    override fun insert(instituicao: Instituicao): Instituicao {
        collection.insertOne(instituicao)
        return instituicao
    }

    override fun update(id: String, updates: Map<String, Any?>): Boolean {
        if (updates.isEmpty()) return false 
        val setUpdates = updates.map { (key, value) -> Updates.set(key, value) }
        val result = collection.updateOneById(id, combine(setUpdates))
        return result.modifiedCount > 0
    }

    override fun delete(id: String): Boolean {
        return collection.deleteOneById(id).deletedCount > 0
    }

    private fun buildFilters () {
        //falta fazer o build dos filtros
    }
}
