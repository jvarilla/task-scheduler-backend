package varilla.joseph.growintandem.modules.http

import org.koin.dsl.module
import varilla.joseph.growintandem.http.HttpRouter
import varilla.joseph.growintandem.http.HttpRouterImpl

val HttpRouterModules = module {
  single { HttpRouterImpl(get(), get()) as HttpRouter }
}
