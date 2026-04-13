// services/FilaService.kt
class FilaService(
    private val filaRepository: FilaRepository,
    private val senhaRepository: SenhaRepository,     // (para contagemSenhas)
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
        // converte String → enum com mensagem clara
        val tipoAtendimento = try {
            Atendimento.valueOf(request.tipoAtendimento)
        } catch (_: Exception) {
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
            acao          = AcaoAuditoria.CRIAR,
            entidade      = "Fila",
            entidadeId    = criada.id,
            usuarioId     = criadorId,
            instituicaoId = instituicaoId
        )
        webSocketManager.broadcast(
            room  = "instituicao:$instituicaoId",
            event = "fila:criada",
            data  = criada
        )
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
                val tipo = try { Atendimento.valueOf(it) }
                    catch (_: Exception) { throw ApiException(400, "Tipo inválido: $it") }
                put("tipoAtendimento", tipo.name)
            }
            request.configuracaoQRCode?.let { put("configuracaoQRCode", it.toModel()) }
            request.mesas?.let              { put("mesas", it.map { m -> m.toModel() }) }
        }

        filaRepository.update(id, updates)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.ATUALIZAR,
            entidade      = "Fila",
            entidadeId    = id,
            usuarioId     = editorId,
            instituicaoId = instituicaoId
        )
        val atualizada = buscarPorId(id)
        webSocketManager.broadcast(
            room  = "instituicao:$instituicaoId",
            event = "fila:atualizada",
            data  = atualizada
        )
        return atualizada
    }

    fun deletar(id: String, deletorId: String, instituicaoId: String) {
        buscarPorId(id)
        filaRepository.delete(id)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.DELETAR,
            entidade      = "Fila",
            entidadeId    = id,
            usuarioId     = deletorId,
            instituicaoId = instituicaoId
        )
        webSocketManager.broadcast(
            room  = "instituicao:$instituicaoId",
            event = "fila:removida",
            data  = mapOf("filaId" to id)
        )
    }

    /** Usa SenhaRepository diretamente — não precisa do SenhaService inteiro */
    fun contagemSenhas(filaId: String): Map<String, Long> {
        buscarPorId(filaId)
        val total = senhaRepository.countByFilaIdAndStatus(filaId, StatusSenha.AGUARDANDO)
        return mapOf("aguardando" to total)
    }

    // ── Extensões de conversão DTO → Model ───────────────────────────────────

    private fun ConfiguracaoQRCodeRequest.toModel() = ConfiguracaoQRCode(
        modoQRCode            = modoQRCode,
        tempoExibicaoMinutos  = tempoExibicaoMin,
        tempoExpiracaoMinutos = tempoExpiracaoMin,
        toleranciaMinutos     = toleranciaMin,
        tempoAlertaSegundos   = tempoAlertaSegundos
    )

    private fun MesaRequest.toModel() = Mesa(
        numero = numero,
        nome   = nome,
        ativa  = ativa
    )
}