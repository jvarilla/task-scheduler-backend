package varilla.joseph.growintandem.modules.domain.plants.objectRepo

import org.koin.dsl.module
import varilla.joseph.growintandem.domain.plants.objectRepo.PlantObjectRepo
import varilla.joseph.growintandem.domain.plants.objectRepo.PlantObjectRepoFileImpl

val PlantObjectRepoModules = module{
  single { PlantObjectRepoFileImpl() as PlantObjectRepo }
}
