package varilla.joseph.growintandem.utils.http

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject

/***
 * Send a message as a json string with a status code
 */
fun HttpServerResponse.sendAsJSONWithStatusCode(message:String, code:Int) {
  this.putHeader("content-type", "application/json")
    .setStatusCode(code)
    .setChunked(true)
    .write(message)
    .end()
}

class RequestErrorException(val statusCode :Int, override val message :String) :Exception()

fun RequestErrorException.toErrorMessageObj() :ErrorMessageObj{
  return ErrorMessageObj(this.statusCode, this.message)
}

fun RequestErrorException.toJsonObject() :JsonObject{
  return JsonObject(Json.encodePrettily(this.toErrorMessageObj()))
}

data class ErrorMessageObj(val statusCode :Int, val message :String)

val SERVER_ERROR_MESSAGE_OBJECT = ErrorMessageObj(500, "Internal Server Error")
