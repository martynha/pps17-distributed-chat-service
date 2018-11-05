package it.unibo.dcs.service.authentication.interactor.usecases.helpers

import it.unibo.dcs.commons.interactor.Validation
import rx.lang.scala.Observable

object ValidationHandler {

  def validateAndContinue[R, U](validation: Validation[R], request: R, action: Unit => Observable[U]): Observable[U] =
    validation(request).flatMap(_ => action(()))

}
