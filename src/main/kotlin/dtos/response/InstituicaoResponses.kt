package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable

@Serializable
data class InstituicaoResponse(
    val id: String,
    val nome: String,
    val cnpj: String,
    val status: String,
    val ativa: Boolean,
    val createdAt: String
)