// services/SenhaService.kt
import java.time.Instant

class SenhaService(
    private val senhaRepository: SenhaRepository,
    private val filaRepository: FilaRepository,
    private val qrCodeRepository: QrCodeRepository,
    private val auditoriaService: AuditoriaService,
    private val webSocketManager: WebSocketManager
) {

    // ── Criação digital (cidadão pelo app) ───────────────────────────────────

    fun criar(
        filaId: String,
        usuarioId: String,
        request: CreateSenhaRequest
    ): Senha {
        val fila = buscarFilaAtiva(filaId)

        verificarDuplicidade(filaId, usuarioId)

        // QR Code obrigatório para filas presenciais/híbridas
        if (fila.tipoAtendimento != Atendimento.ONLINE) {
            val codigo = request.qrCode
                ?: throw ApiException(400, "QR Code obrigatório para esta fila")
            validarQrCode(codigo, filaId)
        }

        val prioridade = validarPrioridade(request.prioridade)
        val posicao    = calcularPosicao(filaId)

        val senha = Senha(
            filaId        = filaId,
            instituicaoId = fila.instituicaoId,
            usuarioId     = usuarioId,
            presencial    = false,
            posicao       = posicao,
            status        = StatusSenha.AGUARDANDO,
            prioridade    = prioridade
        )

        val criada = senhaRepository.insert(senha)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.CRIAR,
            entidade      = "Senha",
            entidadeId    = criada.id,
            usuarioId     = usuarioId,
            instituicaoId = fila.instituicaoId
        )
        emitirEvento("senha:criada", criada, fila.instituicaoId, filaId, usuarioId = null)
        return criada
    }

    // ── Criação presencial (operador cria para cidadão) ──────────────────────

    fun criarPresencial(
        filaId: String,
        operadorId: String,
        request: CreateSenhaPresencialRequest
    ): Senha {
        val fila = buscarFilaAtiva(filaId)

        val prioridade = validarPrioridade(request.prioridade)
        val posicao    = calcularPosicao(filaId)

        val senha = Senha(
            filaId        = filaId,
            instituicaoId = fila.instituicaoId,
            usuarioId     = null,           // sem vínculo com usuário digital
            nomeCidadao   = request.nomeCidadao,
            presencial    = true,
            posicao       = posicao,
            status        = StatusSenha.AGUARDANDO,
            prioridade    = prioridade
        )

        val criada = senhaRepository.insert(senha)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.CRIAR,
            entidade      = "Senha",
            entidadeId    = criada.id,
            usuarioId     = operadorId,
            instituicaoId = fila.instituicaoId,
            dados         = mapOf("presencial" to "true", "nomeCidadao" to request.nomeCidadao)
        )
        emitirEvento("senha:criada", criada, fila.instituicaoId, filaId, usuarioId = null)
        return criada
    }

    // ── Máquina de estados ───────────────────────────────────────────────────

    fun chamar(
        id: String,
        operadorId: String,
        mesa: String? = null,
        mesaNome: String? = null
    ): Senha {
        val senha = buscarPorId(id)

        if (senha.status != StatusSenha.AGUARDANDO)
            throw ApiException(409, "Apenas senhas AGUARDANDO podem ser chamadas")

        val updates = buildMap<String, Any?> {
            put("status", StatusSenha.EM_ATENDIMENTO.name)
            put("operadorId", operadorId)
            mesa?.let    { put("mesa", it) }
            mesaNome?.let { put("mesaNome", it) }
        }

        senhaRepository.update(id, updates)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.ATUALIZAR,
            entidade      = "Senha",
            entidadeId    = id,
            usuarioId     = operadorId,
            instituicaoId = senha.instituicaoId,
            dados         = mapOf("status" to "EM_ATENDIMENTO")
        )
        val atualizada = buscarPorId(id)
        // notifica também o room pessoal do cidadão
        emitirEvento("senha:chamada", atualizada, senha.instituicaoId, senha.filaId, senha.usuarioId)
        return atualizada
    }

    fun cancelar(id: String, solicitanteId: String): Senha {
        val senha = buscarPorId(id)

        if (senha.status != StatusSenha.AGUARDANDO)
            throw ApiException(409, "Apenas senhas AGUARDANDO podem ser canceladas")

        senhaRepository.update(id, mapOf("status" to StatusSenha.CANCELADA.name))
        auditoriaService.registrar(
            acao          = AcaoAuditoria.ATUALIZAR,
            entidade      = "Senha",
            entidadeId    = id,
            usuarioId     = solicitanteId,
            instituicaoId = senha.instituicaoId,
            dados         = mapOf("status" to "CANCELADA")
        )
        val atualizada = buscarPorId(id)
        emitirEvento("senha:atualizada", atualizada, senha.instituicaoId, senha.filaId, senha.usuarioId)
        return atualizada
    }

    fun finalizar(id: String, operadorId: String): Senha {
        val senha = buscarPorId(id)

        if (senha.status != StatusSenha.EM_ATENDIMENTO)
            throw ApiException(409, "Apenas senhas EM_ATENDIMENTO podem ser finalizadas")

        senhaRepository.update(id, mapOf("status" to StatusSenha.FINALIZADA.name))
        auditoriaService.registrar(
            acao          = AcaoAuditoria.ATUALIZAR,
            entidade      = "Senha",
            entidadeId    = id,
            usuarioId     = operadorId,
            instituicaoId = senha.instituicaoId,
            dados         = mapOf("status" to "FINALIZADA")
        )
        val atualizada = buscarPorId(id)
        emitirEvento("senha:finalizada", atualizada, senha.instituicaoId, senha.filaId, senha.usuarioId)
        return atualizada
    }

    // ── Listagem e estatísticas ──────────────────────────────────────────────

    fun listar(
        pagination: PaginationParams,
        filters: Map<String, Any?> = emptyMap()
    ): PaginatedResponse<Senha> {
        val (docs, total) = senhaRepository.findAll(
            page    = pagination.page,
            limit   = pagination.limit,
            filters = filters
        )
        return buildPaginatedResponse(docs, total, pagination)
    }

    fun buscarPorId(id: String): Senha {
        return senhaRepository.findById(id)
            ?: throw ApiException(404, "Senha não encontrada")
    }

    fun stats(instituicaoId: String, timezone: String): SenhaStatsResponse {
        val emAtendimento  = senhaRepository.countByInstituicaoAndStatus(instituicaoId, StatusSenha.EM_ATENDIMENTO)
        val aguardando     = senhaRepository.countByInstituicaoAndStatus(instituicaoId, StatusSenha.AGUARDANDO)
        val finalizadasHoje = senhaRepository.countFinalizadasHoje(instituicaoId, timezone)
        val senhasHoje      = senhaRepository.countCriadasHoje(instituicaoId, timezone)
        val porFila         = senhaRepository.statsPorFila(instituicaoId)

        return SenhaStatsResponse(
            emAtendimento   = emAtendimento,
            aguardando      = aguardando,
            finalizadasHoje = finalizadasHoje,
            senhasHoje      = senhasHoje,
            porFila         = porFila.map {
                SenhaStatsPorFila(
                    filaId        = it.filaId,
                    aguardando    = it.aguardando,
                    emAtendimento = it.emAtendimento
                )
            }
        )
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private fun buscarFilaAtiva(filaId: String): Fila {
        val fila = filaRepository.findById(filaId)
            ?: throw ApiException(404, "Fila não encontrada")
        if (!fila.ativa)
            throw ApiException(409, "Esta fila não está aceitando senhas no momento")
        return fila
    }

    private fun verificarDuplicidade(filaId: String, usuarioId: String) {
        val ativa = senhaRepository.findActivaByUsuarioEFila(usuarioId, filaId)
        if (ativa != null)
            throw ApiException(409, "Você já possui uma senha ativa nesta fila")
    }

    private fun validarQrCode(codigo: String, filaId: String) {
        val qr = qrCodeRepository.findByCodigo(codigo)
            ?: throw ApiException(409, "QR Code inválido")

        if (qr.filaId != filaId)
            throw ApiException(409, "QR Code não pertence a esta fila")

        if (!qr.ativo)
            throw ApiException(409, "QR Code inativo")

        val agora = Instant.now()
        if (agora.isAfter(qr.toleranciaAte))
            throw ApiException(409, "QR Code expirado")
    }

    private fun validarPrioridade(prioridade: String?): String? {
        if (prioridade == null) return null
        val validas = setOf("LEGAL", "FIDELIDADE", "CRONOLÓGICA")
        if (prioridade !in validas)
            throw ApiException(400, "Prioridade inválida: $prioridade")
        return prioridade
    }

    private fun calcularPosicao(filaId: String): Int {
        return (senhaRepository.countByFilaIdAndStatus(filaId, StatusSenha.AGUARDANDO) + 1).toInt()
    }

    private fun emitirEvento(
        event: String,
        senha: Senha,
        instituicaoId: String,
        filaId: String,
        usuarioId: String?
    ) {
        webSocketManager.broadcast("instituicao:$instituicaoId", event, senha)
        webSocketManager.broadcast("fila:$filaId", event, senha)
        usuarioId?.let {
            webSocketManager.broadcast("user:$it", event, senha)
        }
    }
}