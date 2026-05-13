package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable
import br.com.filacidada.models.Papel
@Serializable
data class MeResponse(
    val id: String,
    val nome: String,
    val email: String,
    val papeis: Set<Papel>,
    val ativo: Boolean,
    val avatar: String?,
    val fusoHorario: String,
    val instituicaoId: String?
)

@Serializable
data class TimezoneResponse(
    val fusos: List<String>,
    val padrao: String
)