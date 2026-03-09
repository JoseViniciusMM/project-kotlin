interface QrCodeRepository {
    fun findById(id: String): QrCode?
    fun findByCodigo(codigo: String): QrCode?
    fun findAll(page: Int, limit: Int, filters: Map<String, Any?> = emptyMap()): Pair<List<QrCode>, Long>
    fun findAtivoByFilaId(filaId: String): QrCode?
    fun insert(qrCode: QrCode): QrCode
    fun desativar(id: String): Boolean
}