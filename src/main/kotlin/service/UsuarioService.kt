package br.com.filacidada.service
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.plugins.ApiException
import br.com.filacidada.repositories.UsuarioRepository

class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val auditoriaService: AuditoriaService,
    private val emailService: EmailService
) {
    fun criarParaInstituicao(
        request: CreateUsuarioInstituicaoRequest,
        instituicaoId: String,
        criadorId: String
    ): Usuario {
        if (usuarioRepository.findByEmail(request.email) != null) throw ApiException(400, "E-mail já cadastrado")

        val papeis = request.papeis.map { Papel.valueOf(it) }.toSet()

        val usuario = Usuario(
            nome = request.nome,
            email = request.email,
            papeis = papeis,
            instituicaoId = instituicaoId
        )

        val criado = usuarioRepository.insert(usuario)
        auditoriaService.registrar(AcaoAuditoria.CRIAR.name, "Usuario", criado.id ?: "", criadorId)

        return criado
    }

    fun buscarPorId(id: String): Usuario {
        return usuarioRepository.findById(id) ?: throw ApiException(404, "Usuário não encontrado")
    }

    fun listar(page: Int, limit: Int): List<Usuario> {
        return usuarioRepository.findAll(page, limit, emptyMap()).first
    }

    fun listarPorInstituicao(instituicaoId: String, page: Int, limit: Int): List<Usuario> {
        return usuarioRepository.findByInstituicaoId(instituicaoId, page, limit, emptyMap()).first
    }

    fun atualizar(id: String, dados: Map<String, Any?>): Usuario {
        usuarioRepository.update(id, dados)
        auditoriaService.registrar(AcaoAuditoria.ATUALIZAR.name, "Usuario", id, "sistema")
        return buscarPorId(id)
    }

    fun deletar(id: String) {
        usuarioRepository.delete(id)
        auditoriaService.registrar(AcaoAuditoria.DELETAR.name, "Usuario", id, "sistema")
    }
}