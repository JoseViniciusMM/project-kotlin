import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Instituicao (
    val id: String? = null,
    val nome: String,
    val cnpj: String = "",    
    val email: String = "",
    val telefone: String = "",
    val responsavel: String = "",
    val endereco: String = "",
    val descricao: String = "",
    val ativo: Boolean = true,
    val status: Status = Status.PENDENTE,
    val solicitanteId: String? = null,
    val contratoUrl: String = "",
    val motivoRejeicao: String = "",
    val aprovadoPor: String? = null,
    @Contextual val aprovadoEm: Instant? = null,
    val configuracoes: Map<String, String> = emptyMap(),
    @Contextualval criadoEm: Instant = Instant.now(),
    @Contextualval atualizadoEm: Instant? = null

)


enum class Status {
    PENDENTE,
    APROVADA,
    REJEITADA
}