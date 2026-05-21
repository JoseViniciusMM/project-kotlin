package br.com.filacidada.routes

import br.com.filacidada.models.Papel
import br.com.filacidada.plugins.authorize
import br.com.filacidada.service.LandingPageService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.landingPageRoutes() {
    val landingPageService by inject<LandingPageService>()

    // Rota pública para visualização da Landing Page pelo cliente final
    route("/landing-pages") {
        get("/{instituicaoId}/publica") {
            val instituicaoId = call.parameters["instituicaoId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            call.respond(HttpStatusCode.NotImplemented, "Implementar busca pública da Landing Page")
        }
    }

    // Rotas protegidas para gerenciar a Landing Page
    authenticate("auth-jwt") {
        route("/landing-pages") {

            get {
                call.respond(HttpStatusCode.NotImplemented, "Implementar listagem de Landing Pages")
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.NotImplemented, "Implementar busca de Landing Page por ID")
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                post {
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    call.respond(HttpStatusCode.NotImplemented, "Implementar criação de Landing Page")
                }

                patch("/{id}") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@patch call.respond(HttpStatusCode.Unauthorized)
                    call.respond(HttpStatusCode.NotImplemented, "Implementar atualização de Landing Page")
                }

                delete("/{id}") {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}