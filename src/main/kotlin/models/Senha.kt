import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Senha (
    val id: String? = null,
    val filaId: String,
    val instituicaoId: String,
    val usuarioId: String? = null,
    val nomeCidadao: String? = null,
    val presencial: Boolean = false,
    val posicao: Int,
    val status: StatusSenha = StatusSenha.AGUARDANDO,
    val prioridade: Prioridade? = null,
    val mesa: String? = null,
    val mesaNome: String? = null,
    val operadorId: String? = null,
    @Contextual val criadaEm: Instant = Instant.now();
    @Contextual val atualizadaEm: Instant? = null

)

enum class StatusSenha {
    AGUARDANDO,
    EM_ATENDIMENTO,
    CANCELADA,
    FINALIZADA
}

enum class Prioridade {
    LEGAL,
    FIDELIDADE
}


