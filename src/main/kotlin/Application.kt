package br.com.filacidada
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin

import br.com.filacidada.config.*
import br.com.filacidada.plugins.*
import br.com.filacidada.routes.*

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 7351
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
//    install(Koin) {
//        modules(appModule)
//    }
//
//    val jwtConfig = JwtConfig()
//
//    configureContentNegotiation()
//    configureCORS()
//    configureAuthentication(jwtConfig)
//    configureStatusPages()
//    // configureSwagger()
//
//    routing {
//        authRoutes()
//        usuarioRoutes()
//        perfilRoutes()
//        specialRoutes()
//    }

    routing {
        get("/") {
            call.respondText("🚀 Servidor Ktor da Fila Cidadã rodando perfeitamente na porta 7351!")
        }
    }
}