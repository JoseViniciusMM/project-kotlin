package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable

@Serializable
data class FilaResponse(
    val id: String,
    val instituicaoId: String,
    val nome: String,
    val descricao: String?,
    val status: String,
    val createdAt: String
)