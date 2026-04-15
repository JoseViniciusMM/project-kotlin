import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Updates
import org.litote.kmongo.*

class QrCodeRepositoryImpl (
    private val qrCodeCollection: MongoCollection<QrCode>
) : QrCodeRepository {

    override fun findById(id: String): QrCode? {
        return collection.findOneById(id)
   }

   override fun findByCodigo(codigo: String): QrCode? {
        return collection.findOne(QrCode::codigo eq codigo)
   }

   override fun findAll(page: Int, limit: Int, filters: Map<String, Any?>): Pair<List<QrCode>, Long> {
        val bsonFilters = buildFilters(filters)
        val query = if (bsonFilters.isNotEmpty()) and(bsonFilters) else "{}"
        val total = collection.countDocuments(query as org.bson.conversions.Bson)
        val docs = collection.find(query)
          .skip((page - 1) * limit)
          .limit(limit)
          .toList()
            
     return Pair(docs, total)

   override fun findAtivoByFilaId(filaId: String): QrCode? {
        return collection.findOne(and(QrCode::filaId eq filaId, QrCode::ativo eq true))
   }

   override fun insert(qrCode: QrCode): QrCode {
        collection.insertOne(qrCode)
        return qrCode
   }

   override fun desativar(id: String): Boolean {
        val result = collection.updateOneById(id, Updates.set(QrCode::ativo, false))
        return result.modifiedCount > 0
   }

   private fun buildFilters(filters: Map<String, Any?>): List<org.bson.conversions.Bson> {
        return filters.map { (key, value) ->
            com.mongodb.client.model.Filters.eq(key, value)
        }

}
    
 
