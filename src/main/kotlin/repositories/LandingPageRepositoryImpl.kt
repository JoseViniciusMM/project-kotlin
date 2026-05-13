package br.com.filacidada.repositories
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Updates
import org.litote.kmongo.*
import br.com.filacidada.models.*

class LandingPageRepositoryImpl(
    private val collection: MongoCollection<LandingPage>
) : LandingPageRepository {

    override fun findById(id: String): LandingPage? {
        return collection.findOneById(id)
    }

    override fun getLandingPage(): LandingPage? {
        return collection.findOne(LandingPage::key eq "default")
    }

    override fun insert(landingPage: LandingPage): LandingPage {
        collection.insertOne(landingPage)
        return landingPage
    }

    override fun update(id: String, updates: Map<String, Any?>): Boolean {
        if (updates.isEmpty()) return false
        val setUpdates = updates.map { (key, value) -> Updates.set(key, value) }
        val result = collection.updateOneById(id, Updates.combine(setUpdates))
        return result.modifiedCount > 0
    }

    override fun delete(id: String): Boolean {
        return collection.deleteOneById(id).deletedCount > 0
    }
}