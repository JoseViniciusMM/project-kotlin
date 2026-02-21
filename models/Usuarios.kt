data class usuarios (
    val id: String? = null,
    val id: (ObjectId),
    val nome: String,
    val email: String,
    val senhaHas: String,
    val papel set<Papel>,
    val instituicaoId: String? = null,
    val ativo: Boolean,
    val criadoEm: Instant = Instant.now(),
    val atualizadoEm: Instant? = null
)
enum class Papel (
    ADMIN_PLATAFORMA
    ADMIN_INSTITUICAO
    OPERADOR
    USUARIO_FINAL
)