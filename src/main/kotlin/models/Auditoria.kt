import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class Auditoria (
    val id: String? = null,
    val instituicaoId: String? = null,
    val usuarioId: String? = null,
    val acao: String,
    val entidade: String,
    val entidadeId: String? = null,
    val dados: Map<String, String> = null,
    @Contextual val createdAt: Instant = Instant.now()
)



