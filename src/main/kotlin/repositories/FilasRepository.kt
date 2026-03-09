interface FilasRepository {
    fun findById(id: String): Fila?
    fun findAll(page: Int, limit: Int, filters: Map<String, Any?> = emptyMap()): Pair<List<Fila>, Long>
    fun findByInstituicaoId(instituicaoId: String, page: Int, limit: Int, filters: Map<String, Any?> = emptyMap()): Pair<List<Fila>, Long>
    fun insert(fila: Fila): Fila
    fun update(id: String, updates: Map<String, Any?>): Boolean
    fun delete(id: String): Boolean
}