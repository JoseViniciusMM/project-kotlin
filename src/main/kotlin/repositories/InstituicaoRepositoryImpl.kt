import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Updates
import org.litote.kmongo.*

class InstituicaoRepositoryImpl( 
    private val collection: MongoCollection<Instituicao>
) : InstituicoesRepository {
     
    override fun findById(id: String): Instituicao? {
        return collection.findOneById(id)
    }

    override fun findAll() {

    }

    override fun insert() {

    }

    override fun update() {

    }

    override fun delete() {

    }

    private fun buildFilters () {

    }
}
