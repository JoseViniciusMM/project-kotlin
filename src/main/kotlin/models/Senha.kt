package br.com.filacidada.models

import kotlinx.serialization.Serializable
import br.com.filacidada.utils.InstantSerializer
import java.time.Instant

@Serializable
data class Senha(
    val id: String? = null,
    val filaId: String,
    val instituicaoId: String,
    val usuarioId: String? = null,
    val nomeCidadao: String? = null,
    val presencial: Boolean = false,
    val posicao: Int,
    val status: StatusSenha = StatusSenha.AGUARDANDO,
    val prioridade: Prioridade? = null,
    val operadorId: String? = null,
    val mesa: String? = null,
    val mesaNome: String? = null,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = Instant.now()
)