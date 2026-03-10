class UsuarioService(
    private val usuarioRepository: UsuarioRepository,   // (1) private
    private val auditoriaService: AuditoriaService,     // (1) private
    private val emailService: EmailService              // (1) private
) {

    // ── Método PÚBLICO — interface limpa ──────────────
    fun criarParaInstituicao(
        request: CreateUsuarioInstituicaoRequest,
        instituicaoId: String,
        criadorId: String
    ): Usuario {
        validarEmailUnico(request.email)          // (2) privado
        val papeis = validarPapeis(request.papeis)  // (2) privado
        val usuario = montarEntidade(request, papeis, instituicaoId) // (2)

        val criado = usuarioRepository.insert(usuario)
        auditoriaService.registrar(AcaoAuditoria.CRIAR, "Usuario", criado.id, criadorId)
        enviarEmailBoasVindas(criado)             // (2) privado

        return criado
    }

    // ── Métodos PRIVADOS — detalhes escondidos ────────
    private fun validarEmailUnico(email: String) {          // (3)
        if (usuarioRepository.findByEmail(email) != null)
            throw ApiException(400, "E-mail já cadastrado")
    }

    private fun validarPapeis(papeis: List<String>): Set<Papel> {
        return papeis.map {
            try { Papel.valueOf(it) }
            catch (_: Exception) { throw ApiException(400, "Papel inválido: $it") }
        }.toSet()
    }

    private fun montarEntidade(...): Usuario { ... }
    private fun enviarEmailBoasVindas(u: Usuario) { ... }
}