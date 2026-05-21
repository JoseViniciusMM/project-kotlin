package br.com.filacidada.routes

import br.com.filacidada.models.Papel
import br.com.filacidada.plugins.authorize
import br.com.filacidada.service.SenhaService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.senhaRoutes() {
    val senhaService by inject<SenhaService>()

    route("/filas/{filaId}/senhas") {
        post("/qrcode") {
            call.respond(HttpStatusCode.NotImplemented, "A implementar no Service")
        }
    }

    authenticate("auth-jwt") {
        route("/filas/{filaId}/senhas") {

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO, Papel.ATENDENTE) {
                get {
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }

            post {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                // Usando Map genérico para não quebrar se o DTO não existir
                val request = call.receive<Map<String, String>>()
                call.respond(HttpStatusCode.NotImplemented)
            }

            get("/minha-posicao") {
                call.respond(HttpStatusCode.NotImplemented)
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO, Papel.ATENDENTE) {
                patch("/{id}/chamar") {
                    call.respond(HttpStatusCode.NotImplemented)
                }

                patch("/{id}/finalizar") {
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }

            patch("/{id}/cancelar") {
                val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@patch call.respond(HttpStatusCode.Unauthorized)

                // O erro dizia que exigia 2 argumentos: id e solicitanteId
                val response = senhaService.cancelar(id, userId)
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}