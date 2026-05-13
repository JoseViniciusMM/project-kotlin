package br.com.filacidada.repositories
import br.com.filacidada.models.*

interface LandingPageRepository {
    fun findById(id: String): LandingPage?
    fun getLandingPage(): LandingPage?
    fun insert(landingPage: LandingPage): LandingPage
    fun update(id: String, updates: Map<String, Any?>): Boolean
    fun delete(id: String): Boolean
}