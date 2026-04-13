// dtos/response/SenhaResponses.kt
import kotlinx.serialization.Serializable

@Serializable
data class SenhaStatsPorFila(
    val filaId: String,
    val aguardando: Long,
    val emAtendimento: Long
)

@Serializable
data class SenhaStatsResponse(
    val emAtendimento: Long,
    val aguardando: Long,
    val finalizadasHoje: Long,
    val senhasHoje: Long,
    val porFila: List<SenhaStatsPorFila>
)

@Serializable
data class ContagemSenhasResponse(
    val aguardando: Long
)