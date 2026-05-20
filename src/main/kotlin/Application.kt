package br.com.filacidada

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
    // 1. Liga o Koin (Injeção de dependências e MongoDB)
    install(Koin) {
        modules(appModule)
    }

    val jwtConfig = JwtConfig()

    // 2. Configurações gerais
    configureContentNegotiation()
    configureCORS()
    configureAuthentication(jwtConfig)
    configureStatusPages()

    // 3. Liga o Swagger (já contém o redirecionamento da raiz "/")
    configureSwagger()

    // 4. Agrupa TODAS as suas rotas em um único bloco
    routing {
        authRoutes()
        usuarioRoutes()
        perfilRoutes()
        specialRoutes()

        // Adicione estas linhas se você já tiver os arquivos de rota criados:
        // exampleRoutes()
        // filaRoutes()
        // instituicaoRoutes()
    }
}