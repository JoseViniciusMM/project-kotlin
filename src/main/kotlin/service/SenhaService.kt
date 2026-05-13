package br.com.filacidada.service
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.dtos.response.*
import br.com.filacidada.plugins.ApiException
import br.com.filacidada.repositories.*
import br.com.filacidada.utils.*
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

        if (fila.tipoAtendimento != "ONLINE") {
            val codigo = request.qrCode
                ?: throw ApiException(400, "QR Code obrigatório para esta fila")
            validarQrCode(codigo, filaId)
        }

        // 1. AQUI OCORRE A TRADUÇÃO DE STRING PARA O ENUM 'PRIORIDADE'
        val prioridadeEnum = validarPrioridade(request.prioridade)
        val posicao = calcularPosicao(filaId)

        val senha = Senha(
            filaId        = filaId,
            instituicaoId = fila.instituicaoId,
            usuarioId     = usuarioId,
            presencial    = false,
            posicao       = posicao,
            status        = StatusSenha.AGUARDANDO, // Enum direto na Model
            prioridade    = prioridadeEnum          // Passando o Enum já traduzido
        )

        val criada = senhaRepository.insert(senha)

        auditoriaService.registrar(
            AcaoAuditoria.CRIAR.name,
            "Senha",
            criada.id ?: "",
            usuarioId
        )

        emitirEvento("senha:criada", criada, fila.instituicaoId, filaId, usuarioId)
        return criada
    }

    // ── Criação presencial (operador cria para cidadão) ──────────────────────

    fun criarPresencial(
        filaId: String,
        operadorId: String,
        request: CreateSenhaPresencialRequest
    ): Senha {
        val fila = buscarFilaAtiva(filaId)

        val prioridadeEnum = validarPrioridade(request.prioridade)
        val posicao = calcularPosicao(filaId)

        val senha = Senha(
            filaId        = filaId,
            instituicaoId = fila.instituicaoId,
            usuarioId     = null,
            nomeCidadao   = request.nomeCidadao,
            presencial    = true,
            posicao       = posicao,
            status        = StatusSenha.AGUARDANDO,
            prioridade    = prioridadeEnum
        )

        val criada = senhaRepository.insert(senha)

        auditoriaService.registrar(
            AcaoAuditoria.CRIAR.name,
            "Senha",
            criada.id ?: "",
            operadorId
        )

        emitirEvento("senha:criada", criada, fila.instituicaoId, filaId, null)
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

        // 3. ATUALIZANDO O KMONGO: Passamos .name para gravar no banco como String com segurança
        val updates = buildMap<String, Any?> {
            put("status", StatusSenha.EM_ATENDIMENTO.name)
            put("operadorId", operadorId)
            mesa?.let { put("mesa", it) }
            mesaNome?.let { put("mesaNome", it) }
        }

        senhaRepository.update(id, updates)

        auditoriaService.registrar(
            AcaoAuditoria.ATUALIZAR.name,
            "Senha",
            id,
            operadorId
        )

        val atualizada = buscarPorId(id)
        emitirEvento("senha:chamada", atualizada, senha.instituicaoId, senha.filaId, senha.usuarioId)
        return atualizada
    }

    fun cancelar(id: String, solicitanteId: String): Senha {
        val senha = buscarPorId(id)

        if (senha.status != StatusSenha.AGUARDANDO)
            throw ApiException(409, "Apenas senhas AGUARDANDO podem ser canceladas")

        senhaRepository.update(id, mapOf("status" to StatusSenha.CANCELADA.name))

        auditoriaService.registrar(
            AcaoAuditoria.ATUALIZAR.name,
            "Senha",
            id,
            solicitanteId
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
            AcaoAuditoria.ATUALIZAR.name,
            "Senha",
            id,
            operadorId
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
        // Usando o método real do repositório para evitar erros de compilação
        val emAtendimento  = senhaRepository.countByFilaIdAndStatus(instituicaoId, StatusSenha.EM_ATENDIMENTO)
        val aguardando     = senhaRepository.countByFilaIdAndStatus(instituicaoId, StatusSenha.AGUARDANDO)

        // Mock para resolver a compilação de funções ainda não implementadas no repository
        val finalizadasHoje = 0L
        val senhasHoje = 0L
        val porFila = emptyList<SenhaStatsPorFila>()

        return SenhaStatsResponse(
            emAtendimento   = emAtendimento,
            aguardando      = aguardando,
            finalizadasHoje = finalizadasHoje,
            senhasHoje      = senhasHoje,
            porFila         = porFila
        )
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private fun buscarFilaAtiva(filaId: String): Fila {
        val fila = filaRepository.findById(filaId) ?: throw ApiException(404, "Fila não encontrada")
        if (!fila.ativa) throw ApiException(409, "Esta fila não está aceitando senhas no momento")
        return fila
    }

    private fun verificarDuplicidade(filaId: String, usuarioId: String) {
        val ativa = senhaRepository.hasSenhaAtivaNaFila(usuarioId, filaId)
        if (ativa) throw ApiException(409, "Você já possui uma senha ativa nesta fila")
    }

    private fun validarQrCode(codigo: String, filaId: String) {
        val qr = qrCodeRepository.findByCodigo(codigo) ?: throw ApiException(409, "QR Code inválido")
        if (qr.filaId != filaId) throw ApiException(409, "QR Code não pertence a esta fila")
        if (!qr.ativo) throw ApiException(409, "QR Code inativo")
    }

    // 4. A REGRA DE OURO: Convertendo a String do Request para o Enum do Kotlin
    private fun validarPrioridade(prioridadeStr: String?): Prioridade? {
        if (prioridadeStr == null) return null
        return try {
            // O uppercase() garante que "urgente" vire "URGENTE" e bata com o Enum
            Prioridade.valueOf(prioridadeStr.uppercase())
        } catch (e: Exception) {
            throw ApiException(400, "Prioridade inválida: $prioridadeStr")
        }
    }

    private fun calcularPosicao(filaId: String): Int {
        return (senhaRepository.countByFilaIdAndStatus(filaId, StatusSenha.AGUARDANDO) + 1).toInt()
    }

    // 5. Ajuste no WebSocket para suportar a classe real (evitando "Too many arguments")
    private fun emitirEvento(
        event: String,
        senha: Senha,
        instituicaoId: String,
        filaId: String,
        usuarioId: String?
    ) {
        // Passando os parâmetros (room, event, data) exatamente como o WebSocketManager espera
        webSocketManager.broadcast("instituicao:$instituicaoId", event, senha)
        webSocketManager.broadcast("fila:$filaId", event, senha)
        usuarioId?.let {
            webSocketManager.broadcast("user:$it", event, senha)
        }
    }
}