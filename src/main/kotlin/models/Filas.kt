import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class Fila (
    val id: String? = null,
    val instituicaoId: String,
    val nome: String,
    val tipoAtendimento: Atendimento,
    val ativa: Boolean = true,
    val prioridadesHabilitadas: Boolean = false,
    val fidelidadeHabilitada: Boolean = false,
    val tempoMaximoAtendimento: ConfiguracaoQRCode? = null,
    val configuracaoQRCode: ConfiguracaoQRCode? = null,
    val mesas: List<Mesa> = emptyList(),
    @Contextual val criadoEm: Instant = Instant.now(),
    @Contextual val atualizadoEm: Instant? = null

)

@Serializable
data class ConfiguracaoQRCode (
    val modoQRCode: String = "ROTATIVO",
    val tempoExibicaoMinutos: Int? = null,
    val tempoExpiracaoMinutos: Int? = null,
    val toleranciaMinutos: Int? = null,
    val tempoAlertaSegundos: Int? = null
)

@Serializable
data class Mesa (
    val numero: String,
    val nome: String? = null,
    val ativa: Boolean = true
)

enum class Atendimento (
    ONLINE,
    PRESENCIAL,
    HIBRIDO
)
