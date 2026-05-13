package br.com.filacidada.models

import kotlinx.serialization.Serializable

@Serializable
data class LandingPage(
    val id: String? = null,
    val key: String = "default",
    val titulo: String,
    val conteudo: String
)