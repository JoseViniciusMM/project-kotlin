package br.com.filacidada.service
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.dtos.response.*
import br.com.filacidada.plugins.ApiException
import br.com.filacidada.repositories.*
import br.com.filacidada.utils.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class AuditoriaService(
    private val auditoriaRepository: AuditoriaRepository
) {

    fun registrar(
        acao: String,
        entidade: String,
        entidadeId: String? = null,
        usuarioId: String? = null,
        instituicaoId: String? = null,
        dados: Map<String, String>? = null
    ) {
        val dadosJson: Map<String, JsonElement>? = dados?.mapValues { (_, v) ->
            JsonPrimitive(v)             // converte String → JsonPrimitive
        }

        val registro = Auditoria(
            instituicaoId = instituicaoId,
            usuarioId = usuarioId?: "",
            acao = acao,
            entidade = entidade,
            entidadeId = entidadeId?: "",
            dados = dadosJson
        )

        auditoriaRepository.insert(registro)
    }

    fun listar(
        pagination: PaginationParams,
        filters: Map<String, Any?> = emptyMap()
    ): PaginatedResponse<Auditoria> {
        val (docs, total) = auditoriaRepository.findAll(
            page    = pagination.page,
            limit   = pagination.limit,
            filters = filters
        )
        return buildPaginatedResponse(docs, total, pagination)
    }
}