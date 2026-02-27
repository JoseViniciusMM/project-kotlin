import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class QrCode(
    val id: String,
    val filaId: String? = null,
    val codigo: String,
    val validoAte: Instant = Instant.now(),
    val toleranciaAte: Instant? = null,
    val ativo: Boolean,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    )

fun main() {

}
// (ObjectId)
// (ObjectId)
// (string) – Valor aleatório
// (datetime)
// (datetime)
