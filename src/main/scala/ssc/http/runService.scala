package ssc.http

import cats.effect.{ContextShift, IO, Timer}
import cats.implicits.toSemigroupKOps
import com.typesafe.scalalogging.StrictLogging
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend, UriContext, asStringAlways, basicRequest}
import sttp.tapir.swagger.http4s.SwaggerHttp4s

import scala.concurrent.ExecutionContext

object runService extends App with StrictLogging {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val httpApp = Router("/" -> (httpService.infiniteStreamingRoutes <+> new SwaggerHttp4s(httpService.openApiYml).routes[IO])).orNotFound

  var result = ""

  def stream(): Unit = for {
    response <- BlazeServerBuilder[IO] (ec)
    .bindHttp(8000, "localhost")
    .withHttpApp(httpApp)
    .resource
       .use { _ =>
      IO {
        val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()
        result = basicRequest.response(asStringAlways).get(uri"http://localhost:8000/hello").send(backend).body
        println("Got result: " + result)

        assert(result == "hello world!")

        println("Go to: http://localhost:8000/docs")
        println("Press any key to exit ...")
        scala.io.StdIn.readLine()
      }

    }
      .unsafeRunSync()
  } yield response
  logger.info("Starting the Service")
  stream()

}
