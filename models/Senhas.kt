data class Senhas (
    val _id: String,
    val filaId: String,
    val instituicaoId: String? = null,
    val usuarioId: String? = null
    val posicao: Number,
    val status: Set<Status>,
    val prioridade: String? = null,
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
