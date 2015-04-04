package it.murgia.antonio.sekai.base

import it.murgia.antonio.sekai.base.CanReceiveResponse._
import scala.reflect.runtime.universe._
/**
 * Created by tmnd91 on 04/04/15.
 */
trait InterceptorThing[I] extends ActiveThing[I,Unit]{
  override val outputType  = typeOf[Unit]
  override def canReceiveMessage(from : Identifier, message : Message[I]) = DONTCONSUME
}
