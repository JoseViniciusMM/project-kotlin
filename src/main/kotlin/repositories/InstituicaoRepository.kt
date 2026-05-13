package br.com.filacidada.repositories
import br.com.filacidada.models.*

interface InstituicaoRepository {
    fun findById(id: String): Instituicao?
    fun findByNome(nome: String): Instituicao?
    fun findAll(page: Int, limit: Int, filters: Map<String, Any?>): Pair<List<Instituicao>, Long>
    fun insert(instituicao: Instituicao): Instituicao
    fun update(id: String, updates: Map<String, Any?>): Boolean
    fun delete(id: String): Boolean
}