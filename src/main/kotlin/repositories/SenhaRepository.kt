interface SenhaRepository {
    fun findById(id: String): Senha?
    fun findAll(page: Int, limit: Int, filters: Map<String, Any?> = emptyMap()): Pair<List<Senha>, Long>
    fun insert(senha: Senha): Senha
    fun update(id: String, updates: Map<String, Any?>): Boolean
    fun hasSenhaAtivaNaFila(usuarioId: String, filaId: String): Boolean
    fun getUltimaSenhaDaFila(filaId: String): Int
}
