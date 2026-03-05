import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class LeadingPage(
    val id: String? = null,
    val header: String,
    val hero: String,
    val statistics: String,
    val features: Set<Papel>,
    val steps: String? = null,
    val targets:  String? = null,
    val testimonials: String? = null,
    val faq: String =  String? = null,
    val cta: String? = null,
    val footer: String? = null,
    @Contextual val createdAt: Instant = Instant.now(),
    @Contextual val updatedAt: Instant? = null
)

enum class Statistics {
    ícone,
    valor,
    label
}

enum class Features {
    título,
    subtítulo,
    itens
}

enum class Cta {
    título,
    descrição,
    botões,
}

