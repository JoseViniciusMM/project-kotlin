package br.com.filacidada.service
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.dtos.response.*
import br.com.filacidada.plugins.ApiException
import br.com.filacidada.repositories.*
import br.com.filacidada.utils.*

// services/FilaService.kt
class FilaService(
    private val filaRepository: FilaRepository,
    private val senhaRepository: SenhaRepository,
    private val auditoriaService: AuditoriaService,
    private val webSocketManager: WebSocketManager
) {

    fun listar(
        pagination: PaginationParams,
        filters: Map<String, Any?> = emptyMap()
    ): PaginatedResponse<Fila> {
        val (docs, total) = filaRepository.findAll(
            page    = pagination.page,
            limit   = pagination.limit,
            filters = filters
        )
        return buildPaginatedResponse(docs, total, pagination)
    }

    fun buscarPorId(id: String): Fila {
        return filaRepository.findById(id)
            ?: throw ApiException(404, "Fila não encontrada")
    }

    fun criar(
        request: CreateFilaRequest,
        instituicaoId: String,
        criadorId: String
    ): Fila {
        // Validação da String sem tentar converter para Enum (já que tipoAtendimento na Model é String)
        val tiposValidos = setOf("ONLINE", "PRESENCIAL", "HIBRIDA")
        val tipoAtendimento = request.tipoAtendimento.uppercase()

        if (tipoAtendimento !in tiposValidos) {
            throw ApiException(400, "Tipo de atendimento inválido: ${request.tipoAtendimento}")
        }

        val fila = Fila(
            instituicaoId          = instituicaoId,
            nome                   = request.nome,
            tipoAtendimento        = tipoAtendimento,
            ativa                  = request.ativa,
            prioridadesHabilitadas = request.prioridadesHabilitadas,
            fidelidadeHabilitada   = request.fidelidadeHabilitada,
            configuracaoQRCode     = request.configuracaoQRCode?.toModel(),
            mesas                  = request.mesas.map { it.toModel() }
        )

        val criada = filaRepository.insert(fila)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.CRIAR.name,
            entidade      = "Fila",
            entidadeId    = criada.id ?: "",
            usuarioId     = criadorId,
            instituicaoId = instituicaoId
        )

        // Ajustado para o novo formato simplificado do WebSocketManager
        webSocketManager.broadcast("instituicao:$instituicaoId", "fila:criada", criada)
        return criada
    }

    fun atualizar(
        id: String,
        request: UpdateFilaRequest,
        editorId: String,
        instituicaoId: String
    ): Fila {
        buscarPorId(id)

        val updates = buildMap<String, Any?> {
            request.nome?.let                   { put("nome", it) }
            request.ativa?.let                  { put("ativa", it) }
            request.prioridadesHabilitadas?.let { put("prioridadesHabilitadas", it) }
            request.fidelidadeHabilitada?.let   { put("fidelidadeHabilitada", it) }
            request.tempoMaximoAtendimento?.let { put("tempoMaximoAtendimento", it) }

            request.tipoAtendimento?.let {
                val tiposValidos = setOf("ONLINE", "PRESENCIAL", "HIBRIDA")
                val tipoFormatado = it.uppercase()
                if (tipoFormatado !in tiposValidos) {
                    throw ApiException(400, "Tipo inválido: $it")
                }
                put("tipoAtendimento", tipoFormatado)
            }

            request.configuracaoQRCode?.let { put("configuracaoQRCode", it.toModel()) }
            request.mesas?.let              { put("mesas", it.map { m -> m.toModel() }) }
        }

        filaRepository.update(id, updates)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.ATUALIZAR.name,
            entidade      = "Fila",
            entidadeId    = id,
            usuarioId     = editorId,
            instituicaoId = instituicaoId
        )

        val atualizada = buscarPorId(id)

        // Ajustado para o novo formato simplificado do WebSocketManager
        webSocketManager.broadcast("instituicao:$instituicaoId", "fila:atualizada", atualizada)
        return atualizada
    }

    fun deletar(id: String, deletorId: String, instituicaoId: String) {
        buscarPorId(id)
        filaRepository.delete(id)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.DELETAR.name,
            entidade      = "Fila",
            entidadeId    = id,
            usuarioId     = deletorId,
            instituicaoId = instituicaoId
        )

        // Ajustado para o novo formato simplificado do WebSocketManager
        webSocketManager.broadcast("instituicao:$instituicaoId", "fila:removida", mapOf("filaId" to id))    }

    /** Usa SenhaRepository diretamente — não precisa do SenhaService inteiro */
    fun contagemSenhas(filaId: String): Map<String, Long> {
        buscarPorId(filaId)
        val total = senhaRepository.countByFilaIdAndStatus(filaId, StatusSenha.AGUARDANDO)
        return mapOf("aguardando" to total)
    }

    // ── Extensões de conversão DTO → Model ───────────────────────────────────

    private fun ConfiguracaoQRCodeRequest.toModel() = ConfiguracaoQRCode(
        modoQRCode = this.modoQRCode ?: "ROTATIVO", // Correção: usar 'this.'
        tempoExibicaoMin = this.tempoExibicaoMin,     // Correção: Nomes das variáveis do DTO
        tempoExpiracaoMin = this.tempoExpiracaoMin,
        toleranciaMin = this.toleranciaMin,
        tempoAlertaSegundos = this.tempoAlertaSegundos
    )

    private fun MesaRequest.toModel() = Mesa(
        numero = this.numero,
        nome   = this.nome,
        ativa  = this.ativa
    )
}