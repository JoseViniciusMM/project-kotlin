import com.mongodb.clint.MongoCollection
import org.litote.kmongo.*

class LandingPageRepositoryImpl ( 
    private val collection: MongoCollection<LandingPage>
) : LandingPageRepository {
    
    override fun getLandingPage(): LandingPage? {
        return collection.findOne(LandingPage::key eq "default")
    }
}