interface AuditoriaRepository {
    fun findById(id: String): Auditoria?
    fun findAll(page: Int, limit: Int, filters: Map<String, Any?> = emptyMap()): Pair<List<Auditoria>, Long>
    fun insert(auditoria: Auditoria): Auditoria
}