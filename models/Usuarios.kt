data class usuarios (
    val id: String? = null,
    val nome: String,
    val email: String,
    val senhaHash: String,
    val papel set<Papel>,
    val instituicaoId: set<Instituicao>,
    val ativo: Boolean,
    val avatar: String? = null,
    val acesstoken: String,
    val refreshtoken: String,
    val tokenUnico: String,
    val codigo_recupera_senha: String,
    val exp_codigo_recupera_senha: Instat = Instant.now(),
    val fusoHorario: String,
    val ultimoLoginEm: Instat = Instant.now(),
    val criadoEm: Instant = Instant.now(),
    val atualizadoEm: Instant? = null
)
enum class Papel (
    ADMIN_PLATAFORMA,
    ADMIN_INSTITUICAO,
    OPERADOR,
    USUARIO_FINAL
)
enum class Instituicao (
    ADMIN-INSTITUICAO,
    OPERADOR
)