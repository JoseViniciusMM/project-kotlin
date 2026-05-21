package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable

@Serializable
data class QrCodeResponse(
    val id: String,
    val instituicaoId: String,
    val filaId: String,
    val codigoAcesso: String,
    val urlDestino: String,
    val ativo: Boolean,
    val createdAt: String
)