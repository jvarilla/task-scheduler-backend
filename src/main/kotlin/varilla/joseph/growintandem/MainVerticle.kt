package varilla.joseph.growintandem

import io.vertx.core.Vertx
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.core.module.Module
import varilla.joseph.growintandem.http.HttpRouter
import varilla.joseph.growintandem.modules.application.ApplicationServiceModules
import varilla.joseph.growintandem.modules.application.getVertxModule
import varilla.joseph.growintandem.modules.domain.plants.domainService.PlantDomainServiceModules
import varilla.joseph.growintandem.modules.domain.plants.objectRepo.PlantObjectRepoModules
import varilla.joseph.growintandem.modules.domain.plants.objectRepo.mongo.MongoModules
import varilla.joseph.growintandem.modules.http.HttpRouterModules

class MainVerticle : CoroutineVerticle(), KoinComponent {
  override suspend fun start() {

    val coroutineContext = this.coroutineContext

    // Declare modules and build dependency trees
    startKoin {
      modules(listOf<Module>(
        getVertxModule(Vertx.vertx(), coroutineContext),
        HttpRouterModules, ApplicationServiceModules,
        PlantDomainServiceModules, PlantObjectRepoModules, MongoModules))
    }

    // Create the httpServer
    val httpServer = vertx.createHttpServer()

    // Inject the httpRouter

    val httpRouter by inject<HttpRouter>()

    // Get the router
    val router = httpRouter.getRouter()

    // Start the http server with the router
    httpServer.requestHandler(router)
       .listen(7777)

      println("Verticle Running")
    }


}


