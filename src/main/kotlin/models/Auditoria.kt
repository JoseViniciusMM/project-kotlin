package br.com.filacidada.models

import kotlinx.serialization.Serializable
import br.com.filacidada.utils.InstantSerializer
import java.time.Instant
import kotlinx.serialization.json.JsonElement

@Serializable
data class Auditoria(
    val id: String? = null,
    val acao: String,
    val entidade: String,
    val entidadeId: String,
    val usuarioId: String,
    val instituicaoId: String? = null,
    val dados: Map<String, JsonElement>? = null,
    @Serializable(with = InstantSerializer::class)
    val dataHora: Instant = Instant.now()
)