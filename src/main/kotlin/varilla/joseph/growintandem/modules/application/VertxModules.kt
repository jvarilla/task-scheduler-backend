package varilla.joseph.growintandem.modules.application


import io.vertx.core.Verticle
import io.vertx.core.Vertx
import org.koin.core.module.Module
import org.koin.dsl.module
import varilla.joseph.growintandem.MainVerticle
import kotlin.coroutines.CoroutineContext

fun getVertxModule(vertx :Vertx, coroutineContext :CoroutineContext) : Module {
  return module{
    single { vertx }
    single { coroutineContext }
  }
}
