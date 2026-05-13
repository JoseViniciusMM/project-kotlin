package br.com.filacidada.dtos.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateQrCodeRequest(
    val filaId: String,
    val validoAte: String,
    val toleranciaAte: String
)

@Serializable
data class RegerarQrCodeRequest(
    val filaId: String
)