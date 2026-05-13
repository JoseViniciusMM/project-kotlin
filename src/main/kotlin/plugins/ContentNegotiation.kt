package br.com.filacidada.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

import br.com.filacidada.utils.AppJson

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(AppJson)
    }
}