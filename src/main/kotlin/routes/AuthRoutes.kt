package br.com.filacidada.routes
import br.com.filacidada.config.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import org.koin.ktor.ext.inject
import br.com.filacidada.models.*
import br.com.filacidada.dtos.request.*
import br.com.filacidada.dtos.response.*
import br.com.filacidada.service.*
import br.com.filacidada.utils.*
import br.com.filacidada.plugins.ApiException
import io.ktor.server.auth.authenticate

/**
 * Rotas de autenticação: /auth/... (§9.1 e §9.2)
 */
fun Route.authRoutes() {
    val authService by inject<AuthService>()
    val jwtConfig by inject<JwtConfig>()

    route("/auth") {

        // POST /auth/login — público
        post("/login") {
            val request = call.receive<LoginRequest>()
            val token = authService.login(request)
            call.respondSuccess(token)
        }

        // POST /auth/register — público
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val token = authService.register(request)
            call.respondSuccess(token, "Usuário registrado", HttpStatusCode.Created)
        }

        // POST /auth/refresh — público
        post("/refresh") {
            val request = call.receive<RefreshRequest>()
            val token = authService.refresh(request)
            call.respondSuccess(token)
        }

        // POST /auth/introspect — público
        post("/introspect") {
            val request = call.receive<IntrospectRequest>()
            val decoded = try {
                val jwt = jwtConfig.verifier.verify(request.accessToken)
                IntrospectResponse(
                    active = true,
                    tokenType = "Bearer",
                    exp = jwt.expiresAt?.time?.div(1000),
                    iat = jwt.issuedAt?.time?.div(1000)
                )
            } catch (_: Exception) {
                IntrospectResponse(active = false)
            }
            call.respondSuccess(decoded)
        }

        // POST /auth/logout — autenticado
        authenticate("auth-jwt") {
            post("/logout") {
                val userId = call.currentUserId() ?: throw ApiException(401, "Não autenticado")
                authService.logout(userId)
                call.respondEmptySuccess("Logout realizado")
            }
        }

        // POST /auth/recover — público
        post("/recover") {
            val request = call.receive<RecoverRequest>()
            val result = authService.recover(request)
            call.respondSuccess(result, "E-mail de recuperação enviado")
        }

        // PATCH /auth/password/reset/token — público
        patch("/password/reset/{token}") {
            val token = call.parameters["token"] ?: throw ApiException(400, "Token obrigatório")
            val request = call.receive<ResetSenhaByTokenRequest>()
            authService.resetByToken(token, request)
            call.respondEmptySuccess("Senha redefinida")
        }

        // PATCH /auth/password/reset/code — público
        patch("/password/reset/code") {
            val request = call.receive<ResetSenhaByCodeRequest>()
            authService.resetByCode(request)
            call.respondEmptySuccess("Senha redefinida")
        }
    }
}
