package it.unibo.dcs.commons.service

import io.vertx.scala.ext.web.client.{HttpResponse, WebClient}
import it.unibo.dcs.exceptions.ServiceUnavailableException
import rx.lang.scala.Observable

abstract class AbstractApi(private[this] val discovery: HttpEndpointDiscovery,
                           private[this] val serviceName: String) {

  private[this] val errorObservable = Observable error ServiceUnavailableException(serviceName)

  private[this] var clientOption: Option[WebClient] = None

  discoverClient()

  private[this] def discoverClient(): Unit = {
    discovery.getWebClient(serviceName)
      .subscribe(c => {
        println("Got web client")
        clientOption = Some(c)
      })
  }

  protected final def makeRequest[T](action: WebClient => Observable[HttpResponse[T]]): Observable[HttpResponse[T]] =
    clientOption match {
      case Some(c) => action(c)
        .onErrorResumeNext { cause =>
          clientOption = None
          discoverClient()
          errorObservable
        }
      case _ => errorObservable
    }

}
