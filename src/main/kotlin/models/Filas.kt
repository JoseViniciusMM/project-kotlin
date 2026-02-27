import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class Filas (
    val id: String? = null,
    val instituicaoId: String? = null,
    val nome: String,
    val tipoAtendimento: Set<Atendimento>,
    val ativa: Boolean,
    val prioridadesHabilitadas: Boolean,
    val fidelidadeHabilitada: Boolean
    val tempoMaximoAtendimento: Number
    val configuracaoQRCode:
    val prioridadesHabilitadas: Boolean,
    val configuracaoQRCode: String? = null,
    val criadoEm: Instant = Instant.now(),
    val atualizadoEm: Instant? = null

)

enum class Atendimento (
    ONLINE
    PRESENCIAL
    HIBRIODO
)



// (ObjectId)
// (ObjectId) – Instituição proprietária da fila
// (string) – Nome da fila
// (string) – Online, presencial ou híbrido
// (boolean)
// (boolean)
// (object | null)
// (datetime)
// (datetime)


