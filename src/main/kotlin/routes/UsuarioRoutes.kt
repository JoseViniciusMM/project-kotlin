package br.com.filacidada.routes
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import org.koin.ktor.ext.inject
import io.ktor.server.auth.authenticate
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.service.*
import br.com.filacidada.utils.*
import br.com.filacidada.plugins.authorize

fun Route.usuarioRoutes() {
    val usuarioService by inject<UsuarioService>()

    authenticate("auth-jwt") {
        route("/usuarios") {
            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                get {
                    val pagination = call.parsePagination()
                    val filters = mapOf(
                        "nome" to call.request.queryParameters["nome"],
                        "email" to call.request.queryParameters["email"],
                        "ativo" to call.request.queryParameters["ativo"]?.toBooleanStrictOrNull()
                    )
                    // 👇 Passando page e limit explicitamente para o Service
                    val result = usuarioService.listar(pagination.page, pagination.limit)
                    call.respondSuccess(result)
                }
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                get("/{id}") {
                    val id = call.parameters["id"]!!
                    val usuario = usuarioService.buscarPorId(id)
                    call.respondSuccess(usuario)
                }
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                patch("/{id}") {
                    val id = call.parameters["id"]!!
                    val executorId = call.currentUserId()!!
                    val request = call.receive<UpdateUsuarioRequest>()

                    // Convertendo a Request num Map para o Service não quebrar
                    val dados = buildMap<String, Any?> {
                        request.nome?.let { put("nome", it) }
                        request.ativo?.let { put("ativo", it) }
                        request.instituicaoId?.let { put("instituicaoId", it) }
                    }

                    usuarioService.atualizar(id, dados)
                    val atualizado = usuarioService.buscarPorId(id)
                    call.respondSuccess(atualizado, "Usuário atualizado")
                }
            }

            authorize(Papel.ADMIN_PLATAFORMA) {
                delete("/{id}") {
                    val id = call.parameters["id"]!!
                    usuarioService.deletar(id) // O Service só espera 1 parâmetro
                    call.respondEmptySuccess("Usuário removido")
                }
            }
        }

        route("/instituicoes/{instituicaoId}/usuarios") {
            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                get {
                    val instituicaoId = call.parameters["instituicaoId"]!!
                    val pagination = call.parsePagination()
                    // 👇 Passando page e limit explicitamente
                    val result = usuarioService.listarPorInstituicao(instituicaoId, pagination.page, pagination.limit)
                    call.respondSuccess(result)
                }
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                post {
                    val instituicaoId = call.parameters["instituicaoId"]!!
                    val criadorId = call.currentUserId()!!
                    val request = call.receive<CreateUsuarioInstituicaoRequest>()
                    val criado = usuarioService.criarParaInstituicao(request, instituicaoId, criadorId)
                    call.respondSuccess(criado, "Usuário criado", HttpStatusCode.Created)
                }
            }
        }
    }
}