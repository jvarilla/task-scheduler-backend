package varilla.joseph.growintandem.modules.application

import org.koin.dsl.module
import varilla.joseph.growintandem.application.ApplicationService
import varilla.joseph.growintandem.application.ApplicationServiceImpl

val ApplicationServiceModules = module{
  single { ApplicationServiceImpl() as ApplicationService }
}
