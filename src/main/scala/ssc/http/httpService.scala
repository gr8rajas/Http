package ssc.http

import cats.effect.{ContextShift, IO, Timer}
import cats.implicits.catsSyntaxApplicativeId
import fs2.{Chunk, Stream}
import org.http4s.HttpRoutes
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.HeaderNames
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.{CodecFormat, Schema, endpoint, header, streamBody}

import java.nio.charset.StandardCharsets
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object httpService {
  // mandatory implicits
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val streamingEndpoint = endpoint.get
    .in("hello")
    .out(header[Long](HeaderNames.ContentLength))
    .out(streamBody(Fs2Streams[IO])(Schema.string, CodecFormat.TextPlain(), Some(StandardCharsets.UTF_8)))

  val docs: OpenAPI = OpenAPIDocsInterpreter.toOpenAPI(streamingEndpoint, "Hello", "1.0")
  val openApiYml: String = docs.toYaml

  val infiniteStreamingRoutes: HttpRoutes[IO] = Http4sServerInterpreter.toRoutes(streamingEndpoint) { _ =>

    val output = "hello world!"
    val size = output.length
    Stream.emits(List[String](output))
      .flatMap(list => Stream.chunk(Chunk.seq(list)))
      .metered[IO](100.millis)
      .take(size)
      .covary[IO]
      .map(_.toByte)
      .pure[IO]
      .map(s => Right(size, s))
  }

  println(docs.toYaml)

}
