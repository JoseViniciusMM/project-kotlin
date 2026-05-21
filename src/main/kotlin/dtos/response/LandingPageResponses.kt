package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable

@Serializable
data class LandingPageResponse(
    val id: String,
    val instituicaoId: String,
    val titulo: String,
    val slug: String,
    val conteudoHtml: String?,
    val status: String
)