package varilla.joseph.growintandem

import io.vertx.core.Verticle
import io.vertx.core.Vertx
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.core.module.Module
import varilla.joseph.growintandem.modules.http.HttpRouterModules


fun main() {
      // Inject the vertx context
      val vertx = Vertx.vertx()
      vertx.deployVerticle("varilla.joseph.growintandem.MainVerticle")
}

