package br.com.filacidada.models

import kotlinx.serialization.Serializable
import br.com.filacidada.utils.InstantSerializer
import java.time.Instant

@Serializable
data class QrCode(
    val id: String? = null,
    val filaId: String,
    val codigo: String,
    val ativo: Boolean = true,
    @Serializable(with = InstantSerializer::class)
    val validoAte: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val toleranciaAte: Instant? = null
)