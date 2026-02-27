import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
date class Auditoria (
    val id: String? = null,
    val instituicaoId: String? = null,
    val usuarioId: String? = null,
    val acao: String,
    val entidade: String,
    val entidadeId: String,
    val dado: String? = null,
    val criadoEm: Instant? = null
)


// (ObjectId)
// (ObjectId)
// (ObjectId)
// (string)
// (string)
// (ObjectId | null)
// (object | null)
// (datetime)
