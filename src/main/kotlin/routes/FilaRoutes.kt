package br.com.filacidada.routes

import br.com.filacidada.dtos.request.CreateFilaRequest
import br.com.filacidada.dtos.request.UpdateFilaRequest
import br.com.filacidada.models.Papel
import br.com.filacidada.plugins.authorize
import br.com.filacidada.service.FilaService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.filaRoutes() {
    val filaService by inject<FilaService>()

    authenticate("auth-jwt") {
        route("/instituicoes/{instituicaoId}/filas") {

            get {
                call.respond(HttpStatusCode.NotImplemented, "A implementar no Service")
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                // O erro dizia que só exigia 1 argumento (o ID)
                val response = filaService.buscarPorId(id)
                call.respond(HttpStatusCode.OK, response)
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                post {
                    val instituicaoId = call.parameters["instituicaoId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val request = call.receive<CreateFilaRequest>()
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@post call.respond(HttpStatusCode.Unauthorized)

                    val response = filaService.criar(instituicaoId = instituicaoId, request = request, criadorId = userId)
                    call.respond(HttpStatusCode.Created, response)
                }

                patch("/{id}") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val instituicaoId = call.parameters["instituicaoId"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val request = call.receive<UpdateFilaRequest>()

                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@patch call.respond(HttpStatusCode.Unauthorized)

                    val response = filaService.atualizar(id = id, request = request, instituicaoId = instituicaoId, editorId = userId)
                    call.respond(HttpStatusCode.OK, response)
                }

                patch("/{id}/ativar") {
                    call.respond(HttpStatusCode.NotImplemented)
                }

                patch("/{id}/desativar") {
                    call.respond(HttpStatusCode.NotImplemented)
                }

                delete("/{id}") {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}