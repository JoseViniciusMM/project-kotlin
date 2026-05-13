package br.com.filacidada.models

import kotlinx.serialization.Serializable
import br.com.filacidada.utils.InstantSerializer
import java.time.Instant

@Serializable
data class Usuario(
    val id: String? = null,
    val nome: String,
    val email: String,
    val senhaHash: String = "",
    val papeis: Set<Papel> = emptySet(),
    val ativo: Boolean = true,
    val avatar: String? = null,
    val fusoHorario: String = "America/Manaus",
    val instituicaoId: String? = null,
    val tokenUnico: String? = null,
    val codigoRecuperaSenha: String? = null,
    @Serializable(with = InstantSerializer::class)
    val expCodigoRecuperaSenha: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val ultimoLoginEm: Instant? = null
)