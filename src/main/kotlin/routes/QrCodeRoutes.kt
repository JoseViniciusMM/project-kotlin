package br.com.filacidada.routes

import br.com.filacidada.models.Papel
import br.com.filacidada.plugins.authorize
import br.com.filacidada.service.QrCodeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.qrCodeRoutes() {
    val qrCodeService by inject<QrCodeService>()

    // Rota pública para acessar o link embutido no QR Code
    route("/qr-codes") {
        get("/{codigo}/acessar") {
            val codigo = call.parameters["codigo"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            call.respond(HttpStatusCode.NotImplemented, "Implementar redirecionamento ou leitura do QR Code")
        }
    }

    // Rotas protegidas para geração e gerenciamento
    authenticate("auth-jwt") {
        route("/qr-codes") {

            get {
                call.respond(HttpStatusCode.NotImplemented, "Implementar listagem de QR Codes")
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.NotImplemented, "Implementar busca de QR Code por ID")
            }

            authorize(Papel.ADMIN_PLATAFORMA, Papel.ADMIN_INSTITUICAO) {
                post {
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    call.respond(HttpStatusCode.NotImplemented, "Implementar geração de QR Code")
                }

                patch("/{id}/desativar") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    call.respond(HttpStatusCode.NotImplemented, "Implementar desativação do QR Code")
                }

                delete("/{id}") {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}