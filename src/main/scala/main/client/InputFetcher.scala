package main.client

import sttp.client3.*
import sttp.client3.ziojson.asJson
import zio.*

final case class InputFetcher(sttpBackend: SttpBackend[Task, Any]) {
  def get(url: String): Task[Response[String]] = {
    val uri = uri"$url"
    val request = basicRequest.response(asStringAlways).get(uri)
    sttpBackend.send(request)
  }
}

object InputFetcher {
  val live: ZLayer[SttpBackend[Task, Any], Any, InputFetcher] = ZLayer.fromFunction(new InputFetcher(_))
}
