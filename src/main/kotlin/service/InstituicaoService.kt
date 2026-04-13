// services/InstituicaoService.kt
class InstituicaoService(
    private val instituicaoRepository: InstituicaoRepository,
    private val auditoriaService: AuditoriaService
) {

    // ── CRUD básico ──────────────────────────────────────────────────────────

    fun listar(
        pagination: PaginationParams,
        filters: Map<String, Any?> = emptyMap()
    ): PaginatedResponse<Instituicao> {
        val (docs, total) = instituicaoRepository.findAll(
            page    = pagination.page,
            limit   = pagination.limit,
            filters = filters
        )
        return buildPaginatedResponse(docs, total, pagination)
    }

    fun buscarPorId(id: String): Instituicao {
        return instituicaoRepository.findById(id)
            ?: throw ApiException(404, "Instituição não encontrada")
    }

    fun criar(request: CreateInstituicaoRequest, criadorId: String): Instituicao {
        validarNomeUnico(request.nome)

        val instituicao = Instituicao(
            nome          = request.nome,
            ativo         = request.ativo,
            status        = StatusInstituicao.APROVADA,   // criação direta = já aprovada
            configuracoes = request.configuracoes
        )

        val criada = instituicaoRepository.insert(instituicao)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.CRIAR,
            entidade      = "Instituicao",
            entidadeId    = criada.id,
            usuarioId     = criadorId,
            instituicaoId = criada.id
        )
        return criada
    }

    fun atualizar(
        id: String,
        request: UpdateInstituicaoRequest,
        editorId: String
    ): Instituicao {
        buscarPorId(id)   // garante existência — lança 404 se não achar

        val updates = buildMap<String, Any?> {
            request.nome?.let          { put("nome", it) }
            request.cnpj?.let          { put("cnpj", it) }
            request.email?.let         { put("email", it) }
            request.telefone?.let      { put("telefone", it) }
            request.responsavel?.let   { put("responsavel", it) }
            request.endereco?.let      { put("endereco", it) }
            request.descricao?.let     { put("descricao", it) }
            request.ativo?.let         { put("ativo", it) }
            request.configuracoes?.let { put("configuracoes", it) }
        }

        instituicaoRepository.update(id, updates)
        auditoriaService.registrar(
            acao          = AcaoAuditoria.ATUALIZAR,
            entidade      = "Instituicao",
            entidadeId    = id,
            usuarioId     = editorId,
            instituicaoId = id
        )
        return buscarPorId(id)
    }

    fun deletar(id: String, deletorId: String) {
        buscarPorId(id)
        instituicaoRepository.delete(id)
        auditoriaService.registrar(
            acao       = AcaoAuditoria.DELETAR,
            entidade   = "Instituicao",
            entidadeId = id,
            usuarioId  = deletorId
        )
    }

    // ── Máquina de estados: fluxo de aprovação ───────────────────────────────

    fun solicitar(
        request: SolicitarInstituicaoRequest,
        solicitanteId: String
    ): Instituicao {
        validarNomeUnico(request.nome)

        val instituicao = Instituicao(
            nome         = request.nome,
            cnpj         = request.cnpj ?: "",
            email        = request.email ?: "",
            telefone     = request.telefone ?: "",
            responsavel  = request.responsavel ?: "",
            endereco     = request.endereco ?: "",
            descricao    = request.descricao ?: "",
            status       = StatusInstituicao.PENDENTE,       // estado inicial
            solicitanteId = solicitanteId
        )

        val criada = instituicaoRepository.insert(instituicao)
        auditoriaService.registrar(
            acao       = AcaoAuditoria.CRIAR,
            entidade   = "Instituicao",
            entidadeId = criada.id,
            usuarioId  = solicitanteId,
            dados      = mapOf("status" to "PENDENTE")
        )
        return criada
    }

    fun aprovar(id: String, aprovadorId: String): Instituicao {
        val instituicao = buscarPorId(id)

        // valida estado atual antes de transicionar
        if (instituicao.status != StatusInstituicao.PENDENTE)
            throw ApiException(409, "Apenas instituições PENDENTES podem ser aprovadas")

        instituicaoRepository.update(id, mapOf(
            "status"      to StatusInstituicao.APROVADA.name,
            "aprovadoPor" to aprovadorId,
            "aprovadoEm"  to java.time.Instant.now()
        ))
        auditoriaService.registrar(
            acao       = AcaoAuditoria.ATUALIZAR,
            entidade   = "Instituicao",
            entidadeId = id,
            usuarioId  = aprovadorId,
            dados      = mapOf("status" to "APROVADA")
        )
        return buscarPorId(id)
    }

    fun rejeitar(
        id: String,
        request: RejeitarInstituicaoRequest,
        aprovadorId: String
    ): Instituicao {
        val instituicao = buscarPorId(id)

        if (instituicao.status != StatusInstituicao.PENDENTE)
            throw ApiException(409, "Apenas instituições PENDENTES podem ser rejeitadas")

        instituicaoRepository.update(id, mapOf(
            "status"          to StatusInstituicao.REJEITADA.name,
            "motivoRejeicao"  to (request.motivoRejeicao ?: ""),
            "aprovadoPor"     to aprovadorId,
            "aprovadoEm"      to java.time.Instant.now()
        ))
        auditoriaService.registrar(
            acao       = AcaoAuditoria.ATUALIZAR,
            entidade   = "Instituicao",
            entidadeId = id,
            usuarioId  = aprovadorId,
            dados      = mapOf("status" to "REJEITADA")
        )
        return buscarPorId(id)
    }

    fun reconsiderar(id: String, aprovadorId: String): Instituicao {
        val instituicao = buscarPorId(id)

        if (instituicao.status != StatusInstituicao.REJEITADA)
            throw ApiException(409, "Apenas instituições REJEITADAS podem ser reconsideradas")

        instituicaoRepository.update(id, mapOf(
            "status"         to StatusInstituicao.PENDENTE.name,
            "motivoRejeicao" to ""
        ))
        auditoriaService.registrar(
            acao       = AcaoAuditoria.ATUALIZAR,
            entidade   = "Instituicao",
            entidadeId = id,
            usuarioId  = aprovadorId,
            dados      = mapOf("status" to "PENDENTE")
        )
        return buscarPorId(id)
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private fun validarNomeUnico(nome: String) {
        if (instituicaoRepository.findByNome(nome) != null)
            throw ApiException(409, "Já existe uma instituição com esse nome")
    }
}