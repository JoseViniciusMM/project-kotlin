date class Instituicoes (
    val id: String,
    val nome: String,
    val ativo: Boolean,
    val cnpj: String,
    val email: String,
    val telefone: String,
    val responsavel: String,
    val enderco: String,
    val descricao: String,
    val ativo: Boolean,
    val status: String,
    val solicitanteId: String,
    val contratoUrl: String,
    val motivoRejeicao: String,
    val aprovadoPor: String,
    val aprovadoEm: Instant = Instant.now(),
    val configuracoes: String,
    val criadoEm: Instant = Instant.now(),
    val atualizadoEm: Instant? = null
)

// (ObjectId)
// (string) – Nome oficial da instituição
// (boolean) – Indica se a instituição está ativa
// (object) – Configurações institucionais específicas
// (datetime)
// (datetime)
