package br.com.filacidada.dtos.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val senha: String
)

@Serializable
data class RegisterRequest(
    val nome: String,
    val email: String,
    val senha: String
)

@Serializable
data class RefreshRequest(
    val token: String
)

@Serializable
data class RecoverRequest(
    val email: String
)

@Serializable
data class ResetSenhaByTokenRequest(
    val senha: String
)

@Serializable
data class ResetSenhaByCodeRequest(
    val codigo: String,
    val senha: String
)

@Serializable
data class IntrospectRequest(
    val accessToken: String
)