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

// (ObjectId)
// (ObjectId)
// (string) – Valor aleatório
// (datetime)
// (datetime)
