package it.murgia.antonio.sekai.base.impl

import it.murgia.antonio.sekai.base.Environments.Environment

import scala.reflect.runtime.universe._
import it.murgia.antonio.sekai.base._
import it.murgia.antonio.sekai.base.CanReceiveResponse._
import it.murgia.antonio.sekai.base.exceptions._

class BasicEnvironment extends Environment {
  override protected def actualDispatch[I](from: String, message: Message[I], to: Seq[String])(implicit tagI : TypeTag[I]): Unit = {
    val compatibles = activeThings.filter(_.inputType =:= tagI.tpe)
                                  .map(_.asInstanceOf[ActiveThing[I,_]])
    compatibles.filter(_.canReceiveMessage(from, message) == DONTCONSUME)
               .foreach(_.receiveMessage(from,message))

    compatibles.filter(x => to.contains(x.uniqueName) &&
                            x.canReceiveMessage(from,message) == YES)
               .foreach(_.receiveMessage(from,message))
  }

  /**
   * A signal sent to the environment, who sends the message doesn't expect a response.
   * The first who catches the signal consumes it
   */
  override def signalFirst[I](from: String, message: Message[I])(implicit tagI: TypeTag[I]): Unit = {
    val rec = activeThings.filter(x => x.inputType =:= tagI.tpe).map(_.asInstanceOf[ActiveThing[I,_]])
    rec.filter(_.canReceiveMessage(from,message) == DONTCONSUME).foreach(_.receiveMessage(from,message))
    rec.filter(_.canReceiveMessage(from,message) == YES).headOption match{
      case Some(x : ActiveThing[I,_]) => x.receiveMessage(from,message)
      case None => throw new NoValidRecipientException()
    }
  }

  /**
   * A signal sent to the environment, who sends the message expects a response.
   * The message is consumed when all the Things has received it and provided a response.
   */
  override def signalAllWithResponse[I, O](from: String, message: Message[I])(implicit tagI: TypeTag[I], tagO: TypeTag[O]): Seq[O] = {
    val compatibles = activeThings.filter(x => x.inputType  =:= tagI.tpe)
                                  .map(_.asInstanceOf[ActiveThing[I,_]])
    compatibles.filter(_.canReceiveMessage(from,message) == DONTCONSUME)
               .foreach(_.receiveMessage(from,message))
    compatibles.filter(x => x.outputType =:= tagO.tpe &&
                            x.canReceiveMessage(from,message) == YES)
               .map(_.asInstanceOf[ActiveThing[I,O]].receiveMessage(from,message))
  }

  /**
   * Requests something:
   * I make a Request and I want a Response, that's how things work in real world....!
   * @param from the unique id of the sender
   * @param message the actual message
   * @param to a recipient: the recipient should receive the message and provide a response
   */
  override def unicastRequest[I, O](from: String, message: Message[I], to: String)(implicit tagI: TypeTag[I], tagO: TypeTag[O]): O = {
    val compatibles = activeThings.filter(x => x.inputType  =:= tagI.tpe)
                                  .map(_.asInstanceOf[ActiveThing[I,_]])


    compatibles.filter(_.canReceiveMessage(from,message) == DONTCONSUME)
               .foreach(_.receiveMessage(from,message))
    compatibles.filter(x => to.contains(x.uniqueName) &&
                            x.outputType =:= tagO.tpe &&
                            x.canReceiveMessage(from,message) == YES)
               .headOption match{
                  case Some(x : ActiveThing[I,O]) => x.receiveMessage(from,message)
                  case None => throw new NoValidRecipientException()
               }
  }

  /**
   * An event is a signalAll but it's issued by the environment itself, therefore it hasn't a sender
   */
  override def event[I](message: Message[I])(implicit tagI: TypeTag[I]): Unit = {
    val filtered = activeThings.filter(x => x.inputType =:= tagI.tpe)
                .map(_.asInstanceOf[ActiveThing[I,_]])
      filtered
                .foreach(x => x.canReceiveMessage(NoOne,message) match {
                                case NO => Unit
                                case _ => x.receiveMessage(NoOne, message)
                })
  }

  /**
   * A signal sent to the environment, who sends the message expects a response.
   * The first who catches the signal consumes it and must provide a response
   */
  override def signalFirstWithResponse[I, O](from: String, message: Message[I])(implicit tagI: TypeTag[I], tagO: TypeTag[O]): O = {
    val rec = activeThings.filter(x => x.inputType  =:= tagI.tpe).map(_.asInstanceOf[ActiveThing[I,_]])
    rec.filter(_.canReceiveMessage(from,message) == DONTCONSUME).foreach(_.receiveMessage(from,message))
    rec.filter(x => x.outputType =:= tagO.tpe &&
                    x.canReceiveMessage(from,message) == YES).headOption match{
      case Some(x : ActiveThing[I,O]) => x.receiveMessage(from,message)
      case None => throw new NoValidRecipientException()
    }
  }

  /**
   * A signal sent to the environment, who sends the message doesn't expect a response.
   * The message is consumed when all the Things has received it
   */
  override def signalAll[I](from: String, message: Message[I])(implicit tagI: TypeTag[I]): Unit = {
    activeThings.filter(x => x.inputType  =:= tagI.tpe)
                .map(_.asInstanceOf[ActiveThing[I,_]])
                .foreach(x => x.canReceiveMessage(from,message) match{
                                case YES => List(x.receiveMessage(from,message))
                                case DONTCONSUME => x.receiveMessage(from,message)
                              }
                )
  }

  override protected def actualMulticastRequest[I, O](from: String, message: Message[I], to: Seq[String])(implicit tagI: TypeTag[I], tagO: TypeTag[O]): scala.Seq[O] = {
    val compatibles = activeThings.filter(x => x.inputType  =:= tagI.tpe)
                                  .map(_.asInstanceOf[ActiveThing[I,_]])
    val rec = compatibles.filter(x => to.contains(x.uniqueName))
    compatibles.filter(_.canReceiveMessage(from,message) == DONTCONSUME)
               .foreach(_.receiveMessage(from,message))
    rec.filter( x => x.outputType =:= tagO.tpe && x.canReceiveMessage(from,message) == YES)
       .map(_.asInstanceOf[ActiveThing[I,O]].receiveMessage(from,message))
  }

  override protected def actualInvitation[I](from: String, message: Message[I], to: Seq[String])(implicit tagI: TypeTag[I]): Boolean = {
    val compatibles = activeThings.filter(x => x.inputType =:= tagI.tpe)
                                  .map(_.asInstanceOf[ActiveThing[I,_]])
    val rec = compatibles.filter(x => to.contains(x.uniqueName))
    compatibles.filter(_.canReceiveMessage(from,message) == DONTCONSUME)
               .foreach(_.receiveMessage(from,message))
    rec.filter(_.canReceiveMessage(from,message) == YES)
      .foreach(_.receiveMessage(from,message))
    true
  }
}