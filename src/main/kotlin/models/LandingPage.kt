import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class LandingPage(
    val id: String? = null,
    val key: String = "default",
    val header: Header? = null,
    val hero: Hero? = null,
    val statistics: List<Statistics> = emptyList(),
    val features: FeatureSection? = null,
    val steps: Map<String, String> = emptyMap(),
    val targets: Map<String, String> = emptyMap(),
    val testimonials: Map<String, String> = emptyMap(),
    val faq: List<FaqItem> = emptyList(),
    val cta: CtaSection? = null,
    val footer: Footer? = null,
    @Contextual val createdAt: Instant = Instant.now(),
    @Contextual val updatedAt: Instant? = null
)

@Serializable
data class Header(
    val logo: String,
    val nomeApp: String,
    val textoLogin: String,
    val textoCadastro: String,
)

@Serializable
data class Hero(
    val badge: String,
    val titulo: String,
    val descricao: String, 
    val botoes: List<String>
)

@Serializable
data class StatisticItem(
    val ícone: String,
    val ícone: String,
    val label: String,
)


@Serializable
data class FeatureSection (
    val titulo: String,
    val subtitulo: String,
    val itens: List<String>
)

@Serializable
data class FaqItem (
    val question: String,
    val answer: String
)

@Serializable
data class CtaSection (
    val titulo: String,
    val descricao: String,
    val botoes: List<String>
)

@Serializable
data class Footer(
    val logo: String,
    val nomeApp: String,
    val copyright: String
)