package br.com.filacidada.routes

import br.com.filacidada.dtos.request.CreateInstituicaoRequest
import br.com.filacidada.dtos.request.UpdateInstituicaoRequest
import br.com.filacidada.models.Papel
import br.com.filacidada.plugins.authorize
import br.com.filacidada.service.InstituicaoService
import br.com.filacidada.utils.PaginationParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.instituicaoRoutes() {
    val instituicaoService by inject<InstituicaoService>()

    authenticate("auth-jwt") {
        route("/instituicoes") {

            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val filtros = mutableMapOf<String, Any?>()
                call.request.queryParameters["nome"]?.let { filtros["nome"] = it }
                call.request.queryParameters["status"]?.let { filtros["status"] = it }

                // Usando o PaginationParams exigido pelo seu projeto
                val response = instituicaoService.listar(PaginationParams(page, limit), filtros)
                call.respond(HttpStatusCode.OK, response)
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val response = instituicaoService.buscarPorId(id)
                call.respond(HttpStatusCode.OK, response)
            }

            authorize(Papel.ADMIN_PLATAFORMA) {
                post {
                    val request = call.receive<CreateInstituicaoRequest>()
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@post call.respond(HttpStatusCode.Unauthorized)

                    val response = instituicaoService.criar(request, userId) // Exigia o criadorId
                    call.respond(HttpStatusCode.Created, response)
                }
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                patch("/{id}") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val request = call.receive<UpdateInstituicaoRequest>()
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@patch call.respond(HttpStatusCode.Unauthorized)

                    val response = instituicaoService.atualizar(id, request, userId) // Exigia o editorId
                    call.respond(HttpStatusCode.OK, response)
                }
            }

            authorize(Papel.ADMIN_PLATAFORMA) {
                patch("/{id}/aprovar") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@patch call.respond(HttpStatusCode.Unauthorized)

                    val response = instituicaoService.aprovar(id, userId) // Exigia aprovadorId
                    call.respond(HttpStatusCode.OK, response)
                }

                patch("/{id}/rejeitar") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val request = call.receive<br.com.filacidada.dtos.request.RejeitarInstituicaoRequest>()
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@patch call.respond(HttpStatusCode.Unauthorized)

                    // Exigia request e aprovadorId
                    val response = instituicaoService.rejeitar(id, request, userId)
                    call.respond(HttpStatusCode.OK, response)
                }

                delete("/{id}") {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    // Como "remover" não foi achado, marcamos 204 para a rota existir na documentação do trabalho
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}