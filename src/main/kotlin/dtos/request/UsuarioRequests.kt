package br.com.filacidada.dtos.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateUsuarioInstituicaoRequest(
    val nome: String,
    val email: String,
    val papeis: List<String>
)

@Serializable
data class UpdateUsuarioRequest(
    val nome: String? = null,
    val papeis: List<String>? = null,
    val ativo: Boolean? = null,
    val instituicaoId: String? = null
)