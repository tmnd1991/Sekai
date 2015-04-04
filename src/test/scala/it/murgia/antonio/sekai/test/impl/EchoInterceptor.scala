package it.murgia.antonio.sekai.test.impl

import it.murgia.antonio.sekai.base.Environments.Environment
import it.murgia.antonio.sekai.base._

import scala.reflect.runtime.universe._
/**
 * Created by tmnd91 on 04/04/15.
 */
class EchoInterceptor(implicit env: Environment) extends Thing("EchoInterceptor")(env) with InterceptorThing[String] {
  override val inputType  = typeOf[String]
  var _counter = 0
  def counter = _counter

  override def receiveMessage(from: Identifier, message: Message[String]) = {
    _counter += 1
  }
}
