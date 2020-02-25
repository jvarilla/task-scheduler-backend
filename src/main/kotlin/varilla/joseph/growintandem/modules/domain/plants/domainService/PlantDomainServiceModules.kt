package varilla.joseph.growintandem.modules.domain.plants.domainService

import org.koin.dsl.module
import varilla.joseph.growintandem.domain.plants.domainService.PlantDomainService
import varilla.joseph.growintandem.domain.plants.domainService.PlantDomainServiceImpl

val PlantDomainServiceModules = module {
  single{ PlantDomainServiceImpl() as PlantDomainService }
}
