data class Senhas (
    val id: String,
    val filaId: String,
    val instituicaoId: String? = null,
    val usuarioId: String? = null,
    val nomeCidadao: String,
    val presencial: Boolean,
    val posicao: Number,
    val status: Set<Status>,
    val prioridade: String? = null,
    val mesa: String? = null,
    val mesaNome: String? = null,
    val operadorId: String? = null,
    val criadaEm: Instant = Instant.now();
    val atualizadaEm: Instant? = null

)

enum class Status (
    AGUARDANDO
    EM_ATENDIMENTO
    CANCELADA
    FINALIZADA
)



// (ObjectId)
// (ObjectId)
// (ObjectId)
// (ObjectId)
// (number)
// (string) – Aguardando, em atendimento, cancelada, finalizada
// (string | null)
// (datetime)
// (datetime)
