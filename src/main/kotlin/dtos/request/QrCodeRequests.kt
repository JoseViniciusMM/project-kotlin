// dtos/request/QrCodeRequests.kt
import kotlinx.serialization.Serializable

@Serializable
data class CreateQrCodeRequest(
    val filaId: String,          // qual fila este QR Code atende
    val validoAte: String,       // ISO 8601 — expiração principal
    val toleranciaAte: String    // ISO 8601 — fim da janela de tolerância
    
)

@Serializable
data class RegerarQrCodeRequest(
    val filaId: String           // Service busca a fila, desativa QR anterior e cria novo
)