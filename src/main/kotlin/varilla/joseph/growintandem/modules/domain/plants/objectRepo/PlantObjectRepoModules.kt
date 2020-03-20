package varilla.joseph.growintandem.modules.domain.plants.objectRepo

import org.koin.dsl.module
import varilla.joseph.growintandem.domain.plants.objectRepo.PlantObjectRepo
import varilla.joseph.growintandem.domain.plants.objectRepo.PlantObjectRepoFileImpl
import varilla.joseph.growintandem.domain.plants.objectRepo.PlantObjectRepoMongoImpl

val PlantObjectRepoModules = module{
  single { PlantObjectRepoMongoImpl() as PlantObjectRepo }
}
