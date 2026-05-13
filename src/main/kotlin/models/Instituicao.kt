package br.com.filacidada.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import br.com.filacidada.utils.InstantSerializer
import java.time.Instant

@Serializable
data class Instituicao(
    val id: String? = null,
    val nome: String,
    val cnpj: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val responsavel: String? = null,
    val endereco: String? = null,
    val descricao: String? = null,
    val status: StatusInstituicao = StatusInstituicao.PENDENTE,
    val ativo: Boolean = true,
    val configuracoes: Map<String, JsonElement> = emptyMap(),
    val solicitanteId: String? = null,
    val aprovadoPor: String? = null,
    @Serializable(with = InstantSerializer::class)
    val aprovadoEm: Instant? = null,
    val motivoRejeicao: String? = null
)