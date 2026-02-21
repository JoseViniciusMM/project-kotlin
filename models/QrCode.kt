data class QrCode(
    val _id: String,
    val filaId: String? = null,
    val codigo: String,
    val validoAte: Instant = Instant.now(),
    val toleranciaAte: Instant? = null
)

// (ObjectId)
// (ObjectId)
// (string) – Valor aleatório
// (datetime)
// (datetime)
