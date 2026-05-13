package br.com.filacidada.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import br.com.filacidada.models.Papel
import br.com.filacidada.utils.currentUserPapeis
import br.com.filacidada.utils.respondError

class AuthorizedRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Constant
}

fun Route.authorize(vararg roles: Papel, build: Route.() -> Unit) {
    val route = createChild(AuthorizedRouteSelector())
    route.intercept(ApplicationCallPipeline.Plugins) {
        val userRoles = call.currentUserPapeis()
        if (userRoles.none { it in roles }) {
            call.respondError("Acesso negado: permissões insuficientes", status = HttpStatusCode.Forbidden)
            finish()
        }
    }
    route.build()
}