package br.com.filacidada.models

import kotlinx.serialization.Serializable

@Serializable
data class ConfiguracaoQRCode(
    val modoQRCode: String = "ROTATIVO",
    val tempoExibicaoMin: Int? = null,
    val tempoExpiracaoMin: Int? = null,
    val toleranciaMin: Int? = null,
    val tempoAlertaSegundos: Int? = null
)

@Serializable
data class Mesa(
    val numero: String,
    val nome: String? = null,
    val ativa: Boolean = true
)

@Serializable
data class Fila(
    val id: String? = null,
    val instituicaoId: String,
    val nome: String,
    val tipoAtendimento: String,
    val ativa: Boolean = true,
    val prioridadesHabilitadas: Boolean = false,
    val fidelidadeHabilitada: Boolean = false,
    val tempoMaximoAtendimento: Int? = null,
    val configuracaoQRCode: ConfiguracaoQRCode? = null,
    val mesas: List<Mesa> = emptyList()
)