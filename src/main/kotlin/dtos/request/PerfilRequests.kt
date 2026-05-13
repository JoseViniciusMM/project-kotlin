package br.com.filacidada.dtos.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePerfilRequest(
    val nome: String? = null,
    val email: String? = null,
    val fusoHorario: String? = null
)

@Serializable
data class AlterarSenhaRequest(
    val senhaAtual: String,
    val novaSenha: String
)