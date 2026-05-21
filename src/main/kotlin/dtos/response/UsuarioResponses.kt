package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioResponse(
    val id: String,
    val nome: String,
    val email: String,
    val papeis: List<String>,
    val instituicaoId: String?,
    val ativo: Boolean,
    val createdAt: String
)