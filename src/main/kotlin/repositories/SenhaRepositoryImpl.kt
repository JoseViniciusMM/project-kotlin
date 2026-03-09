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

    override fun findById() {

    }

    override fun findAll() {

    }

    override fun insert() {

    }

    override fun update() {

    }

    override fun hasSenhaAtivaNaFila() {

    }

    override fun getUltimaSenhaDaFila() {

    }

    private fun buildFilters () {

    }
}