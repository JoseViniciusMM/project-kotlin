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

   override fun findAll() {

   }

   override fun findAtivoByFilaId() {

   }

   override fun insert() {

   }

   override fun desativar() {

   }

    private fun buildFilters () {

    }

}
    
 
