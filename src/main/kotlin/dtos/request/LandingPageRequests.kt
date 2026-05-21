package br.com.filacidada.dtos.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateLandingPageRequest(
    val titulo: String,
    val slug: String,
    val conteudoHtml: String? = null
)

@Serializable
data class UpdateLandingPageRequest(
    val titulo: String? = null,
    val slug: String? = null,
    val conteudoHtml: String? = null,
    val status: String? = null
)