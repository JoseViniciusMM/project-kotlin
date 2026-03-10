import com.mongodb.client.MongoCollection
import org.litote.kmongo.*

class AuditoriaRepositoryImpl (
    private val collection: MongoCollection<Auditoria>
) : AuditoriaRepository {

    override fun findById(id: String): Auditoria? {
        return collection.findOneById(id)
    }

    override fun findAll(page: Int, limit: Int, filters: Map<String, String?>): Pair<List<Auditoria>, Long>
    {
        val bsonFilters = buildFilters(filters)
        val query = if (bsonFilters.isNotEmpty()) and (*bsonFilters.toTypedArray()) else null
        val total = collection.countDocuments(query as org.bson.conversions.Bson)
        val docs = collection.find(query)
            .sort(descending(Auditoria::createdAt))
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
            
        return Pair(docs, total)
    }

    override  fun insert(auditoria: Auditoria): Auditoria {
        collection.insertOne(auditoria)
        return auditoria
    }

    private fun buildFilters(){
        //falta fazer o build dos filtros
    }
}