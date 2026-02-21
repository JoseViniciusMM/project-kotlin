date class Instituicoes (
    val _id: String,
    val nome: String,
    val ativo: Boolean,
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
