package br.com.filacidada.utils
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import br.com.filacidada.models.Papel

fun ApplicationCall.currentUserId(): String? {
    return principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString()
}

fun ApplicationCall.currentUserPapeis(): Set<Papel> {
    val papeisList = principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("papeis")
        ?.asList(String::class.java)
        ?: return emptySet()

    return papeisList.mapNotNull { nome ->
        try { Papel.valueOf(nome) } catch (_: IllegalArgumentException) { null }
    }.toSet()
}

fun ApplicationCall.currentInstituicaoId(): String? {
    return principal<JWTPrincipal>()?.payload?.getClaim("instituicaoId")?.asString()
}

fun ApplicationCall.hasAnyRole(vararg papeis: Papel): Boolean {
    return currentUserPapeis().any { it in papeis }
}