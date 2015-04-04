package it.murgia.antonio.sekai.base

import it.murgia.antonio.sekai.base.Environments.Environment


import scala.reflect.runtime.universe._

/**
 * This trait represents a Thing.
 * Thing are real world objects therefore they exists in an environment. 
 * (Have you ever seen something (some Thing) without its surroundings? I think not.
 * Then a Thing exists only associate with its environment (s?)
 * Things can issue messages to their environment, there are different types of messages:
 * <ul>
 * <li>Request : point to point message with response</li>
 * <li>Signal  : </li>
 * <li>Event   : broadcast message, usually issued by the environment itself</li>
 * <li></li>
 * <li></li>
 * </ul>
 */

abstract class Thing(val uniqueName : Identifier)(implicit env: Environment){
  val environment = env
  /**
   * REQUIREMENTS
   */
  require(environment != null)
  /**
   * END REQUIREMENTS 
   */
  def dispatch[I](message : Message[I], to : Identifier*)(implicit tagI : TypeTag[I]) =
    environment.dispatch(uniqueName,message,to:_*)

  def invitation[I](message : Message[I], to : Identifier*)(implicit tagI : TypeTag[I]) =
    environment.invitation[I](uniqueName,message,to:_*)

  def unicastRequest[I,O](message : Message[I], to : Identifier)(implicit tagI : TypeTag[I], tagO : TypeTag[O]) =
    environment.unicastRequest[I,O](uniqueName,message,to)

  def multicastRequest[I,O](message : Message[I], to : Identifier*)(implicit tagI : TypeTag[I], tagO : TypeTag[O]) =
    environment.multicastRequest[I,O](uniqueName,message,to:_*)

  def signalFirst[I](message : Message[I])(implicit tagI : TypeTag[I]) =
    environment.signalFirst[I](uniqueName,message)

  def signalAll[I](message : Message[I])(implicit tagI : TypeTag[I]) =
    environment.signalAll[I](uniqueName,message)

  def signalFirstWithResponse[I, O](message : Message[I])(implicit tagI : TypeTag[I], tagO : TypeTag[O]) =
    environment.signalFirstWithResponse[I,O](uniqueName,message)

  def signalAllWithResponse[I,O](message : Message[I])(implicit tagI : TypeTag[I], tagO : TypeTag[O]) =
    environment.signalAllWithResponse[I,O](uniqueName,message)

  def event[I](message : Message[I])(implicit tagI : TypeTag[I]) =
    environment.event[I](message)
}