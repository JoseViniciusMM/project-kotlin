package br.com.filacidada.service
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.dtos.response.*
import br.com.filacidada.plugins.ApiException
import br.com.filacidada.repositories.*
import br.com.filacidada.utils.*
import java.util.UUID

class QrCodeService(
    private val qrCodeRepository: QrCodeRepository,
    private val filaRepository: FilaRepository,
    private val auditoriaService: AuditoriaService
) {

    fun gerarNovoQrCodeParaFila(filaId: String, criadorId: String): QrCode {
        // 1. Valida se a fila existe
        val fila = filaRepository.findById(filaId)
            ?: throw ApiException(404, "Fila não encontrada")

        // 2. Desativa o QR Code atual (se existir algum ativo)
        val qrCodeAtual = qrCodeRepository.findAtivoByFilaId(filaId)
        if (qrCodeAtual != null && qrCodeAtual.id != null) {
            qrCodeRepository.desativar(qrCodeAtual.id)
            auditoriaService.registrar(AcaoAuditoria.ATUALIZAR.name, "QrCode", qrCodeAtual.id, criadorId)
        }

        // 3. Cria o novo QR Code
        val novoCodigo = UUID.randomUUID().toString()
        val novoQrCode = QrCode(
            filaId = filaId,
            codigo = novoCodigo,
            ativo = true
        )

        val salvo = qrCodeRepository.insert(novoQrCode)
        auditoriaService.registrar(AcaoAuditoria.CRIAR.name, "QrCode", salvo.id ?: "", criadorId)

        return salvo
    }

    fun buscarAtivoDaFila(filaId: String): QrCode {
        return qrCodeRepository.findAtivoByFilaId(filaId)
            ?: throw ApiException(404, "Nenhum QR Code ativo encontrado para esta fila")
    }

    fun buscarPorCodigo(codigo: String): QrCode {
        return qrCodeRepository.findByCodigo(codigo)
            ?: throw ApiException(404, "QR Code inválido ou não encontrado")
    }

    fun desativarQrCode(id: String, autorId: String) {
        val qrCode = qrCodeRepository.findById(id)
            ?: throw ApiException(404, "QR Code não encontrado")

        qrCodeRepository.desativar(id)
        auditoriaService.registrar(AcaoAuditoria.ATUALIZAR.name, "QrCode", id, autorId)
    }
}