package br.com.filacidada.repositories
import br.com.filacidada.models.*

interface AuditoriaRepository {
    fun findById(id: String): Auditoria?
    fun findAll(page: Int, limit: Int, filters: Map<String, Any?>): Pair<List<Auditoria>, Long>
    fun insert(auditoria: Auditoria): Auditoria
}