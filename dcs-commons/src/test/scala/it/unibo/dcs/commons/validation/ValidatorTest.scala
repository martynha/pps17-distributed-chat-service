package it.unibo.dcs.commons.validation

import org.scalatest.FlatSpec
import rx.lang.scala.Observable

final class ValidatorTest extends FlatSpec {

  val validator: Validator[String] = Validator[String] {
    _
      .addRule({
        Observable.just(_).filter(_ != null)
          .singleOption
          .map(opt => opt.getOrElse(throw new NullPointerException("string is null")))
      })
      .addRule({
        Observable.just(_).filter(_.length > 0)
          .singleOption
          .map(opt => opt.getOrElse(throw new IllegalArgumentException("String is empty")))
      })
      .addRule({
        Observable.just(_).filter(_.length < 15)
          .singleOption
          .map(opt => opt.getOrElse(throw new IllegalArgumentException("Length is greater than 15")))
      })
  }

  it should "throw a NullPointerException" in {
    validator.validate(null).subscribe(_ => (), ex => assert(ex.isInstanceOf[NullPointerException]))
  }

  it should "throw a IllegalArgumentException with message \"String is empty\"" in {
    validator.validate("").subscribe(_ => (),
      ex => assert(ex.isInstanceOf[IllegalArgumentException] && ex.getMessage == "String is empty"))
  }

  it should "throw a IllegalArgumentException with message \"Length is greater than 15\"" in {
    validator.validate("reallylongusername").subscribe(_ => (),
      ex => assert(ex.isInstanceOf[IllegalArgumentException] && ex.getMessage == "Length is greater than 15"))
  }

  it should "not throw an exception" in {
    validator.validate("mvandi").subscribe()
  }

  it should "not IllegalStateException since builder has already been built" in {
    var builder: ValidatorBuilder[String] = null

    Validator[String] { b =>
      builder = b
    }
    val thrown = intercept[IllegalStateException] {
      builder.build
    }
    assert(thrown.getMessage == "builder has already been built")
  }

}
