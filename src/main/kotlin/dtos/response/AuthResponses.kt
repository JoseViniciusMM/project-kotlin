package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String,
    val refresh: String,
    val expiraEm: String
)

@Serializable
data class IntrospectResponse(
    val active: Boolean,
    val clientId: String? = null,
    val tokenType: String? = null,
    val exp: Long? = null,
    val iat: Long? = null,
    val nbf: Long? = null
)