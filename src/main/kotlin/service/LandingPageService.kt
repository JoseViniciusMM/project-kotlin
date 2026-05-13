package br.com.filacidada.service
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.dtos.response.*
import br.com.filacidada.plugins.ApiException
import br.com.filacidada.repositories.*
import br.com.filacidada.utils.*

class LandingPageService(
    private val landingPageRepository: LandingPageRepository,
    private val auditoriaService: AuditoriaService
) {

    fun buscarPorId(id: String): LandingPage {
        return landingPageRepository.findById(id)
            ?: throw ApiException(404, "Landing Page não encontrada")
    }

    fun criar(landingPage: LandingPage, criadorId: String): LandingPage {
        val salva = landingPageRepository.insert(landingPage)

        auditoriaService.registrar(AcaoAuditoria.CRIAR.name, "LandingPage", salva.id ?: "", criadorId)

        return salva
    }

    fun atualizar(id: String, dados: Map<String, Any?>, autorId: String): LandingPage {
        // Valida se existe antes de atualizar
        buscarPorId(id)

        val atualizado = landingPageRepository.update(id, dados)
        if (!atualizado) {
            throw ApiException(400, "Nenhuma alteração foi realizada na Landing Page")
        }

        auditoriaService.registrar(AcaoAuditoria.ATUALIZAR.name, "LandingPage", id, autorId)

        return buscarPorId(id)
    }

    fun deletar(id: String, autorId: String) {
        // Valida se existe
        buscarPorId(id)

        landingPageRepository.delete(id)
        auditoriaService.registrar(AcaoAuditoria.DELETAR.name, "LandingPage", id, autorId)
    }
}