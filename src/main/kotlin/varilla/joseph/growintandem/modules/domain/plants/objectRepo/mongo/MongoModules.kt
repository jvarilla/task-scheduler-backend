package varilla.joseph.growintandem.modules.domain.plants.objectRepo.mongo

import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.jsonObjectOf
import org.koin.dsl.module

val MONGO_DB_NAME = "testDB"
val MONGO_CONNECTION_STRING = "mongodb://localhost:27017"
val MONGO_CONFIG = jsonObjectOf(
  // Single Cluster Settings
  "db_name" to "testDB",
  "connection_string" to "mongodb://127.0.0.1:27017"
)

val MongoModules = module {
  single { MongoClient.createShared(get(), MONGO_CONFIG)  as MongoClient }
}
