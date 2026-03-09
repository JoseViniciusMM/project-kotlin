import com.mongodb.client.MongoCollection
import org.litote.kmongo.*

class AuditoriaRepositoryImpl (
    private val collection: MongoCollection<Auditoria>
) : AuditoriaRepository {

    override fun findById(id: String): Auditoria? {
        return collection.findOneById(id)
    }

    override fun findAll() {

        
    }

    override  fun insert(auditoria: Auditoria): Auditoria {
        collection.insertOne(auditoria)
        return auditoria
    }

    private fun buildFilters(){
        
    }
}