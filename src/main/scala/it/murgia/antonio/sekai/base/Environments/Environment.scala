package it.murgia.antonio.sekai.base.Environments

import it.murgia.antonio.sekai.base._

import scala.reflect.runtime.universe._

trait Environment {
  private var _things = List[Thing]()

  private var _activeThings = List[ActiveThing[_,_]]()

  def activeThings = _activeThings.toSeq

  def things = _things.toSeq

  def register(t : Thing) = {
    t match {
      case at: ActiveThing[_, _] => _activeThings :+= at
    }
    _things :+= t
  }


  /**
   * Dispatches a message:
   * there are no guarantees that it will be processed,
   * and the sender, cannot know it has been processed or not.
   * @param from the unique id of the sender
   * @param message the actual message
   * @param to a list of recipients, the message will sent to every recipient.
   */
  final def dispatch[I](from : Identifier, message : Message[I], to : Identifier*)(implicit tagI : TypeTag[I]) : Unit = {
    require(to.nonEmpty)
    actualDispatch(from, message, to)
  }

  protected def actualDispatch[I](from : Identifier, message : Message[I], to : Seq[Identifier])(implicit tagI : TypeTag[I]) : Unit
  /**
   * Invitates something:
   * sends a message, an ack is received when the messagging system receives the intent to communicate.
   * @param from the unique id of the sender
   * @param message the actual message
   * @param to a list of recipients, the message is sent to every recipient, when the environment
   * has dispatched the message to every recipient returns true.
   */
  final def invitation[I](from : Identifier, message : Message[I], to : Identifier*)(implicit tagI: TypeTag[I]) : Boolean = {
    require(to.nonEmpty)
    actualInvitation(from, message, to)
  }
  
  protected def actualInvitation[I](from : Identifier, message : Message[I], to : Seq[Identifier])(implicit tagI: TypeTag[I]) : Boolean
  
  /**
   * Requests something:
   * I make a Request and I want a Response, that's how things work in real world....! 
   * @param from the unique id of the sender
   * @param message the actual message
   * @param to a recipient: the recipient should receive the message and provide a response
   */
  def unicastRequest[I,O](from : Identifier, message : Message[I], to : Identifier)(implicit tagI: TypeTag[I], tagO: TypeTag[O]) : O
  
  /**
   * Requests something:
   * I make a Request and I want a Response, that's how things work in real world....! 
   * @param from the unique id of the sender
   * @param message the actual message
   * @param to a list of recipients: the recipients should receive the message and provide a response (everyONE)
   */
  final def multicastRequest[I,O](from : Identifier, message : Message[I], to : Identifier*)(implicit tagI: TypeTag[I], tagO: TypeTag[O]) : Seq[O] = {
    require(to.nonEmpty)
    actualMulticastRequest[I,O](from, message, to)
  }
  
  protected def actualMulticastRequest[I,O](from : Identifier, message : Message[I], to : Seq[Identifier])(implicit tagI: TypeTag[I], tagO: TypeTag[O]) : Seq[O]
  
  /**
   * A signal sent to the environment, who sends the message doesn't expect a response.
   * The first who catches the signal consumes it
   */
  def signalFirst[I](from : Identifier, message : Message[I])(implicit tagI: TypeTag[I]) : Unit
  /**
   * A signal sent to the environment, who sends the message doesn't expect a response.
   * The message is consumed when all the Things has received it
   */
  def signalAll[I](from : Identifier, message : Message[I])(implicit tagI: TypeTag[I]) : Unit
  /**
   * A signal sent to the environment, who sends the message expects a response.
   * The first who catches the signal consumes it and must provide a response
   */
  def signalFirstWithResponse[I, O](from : Identifier, message : Message[I])(implicit tagI: TypeTag[I], tagO: TypeTag[O]) : O
  /**
   * A signal sent to the environment, who sends the message expects a response.
   * The message is consumed when all the Things has received it and provided a response.
   */
  def signalAllWithResponse[I,O](from : Identifier, message : Message[I])(implicit tagI: TypeTag[I], tagO: TypeTag[O]) : Seq[O]
  
  /**
   * An event is a signalAll but it's issued by the environment itself, therefore it hasn't a sender
   */
  def event[I](message : Message[I])(implicit tagI: TypeTag[I]) : Unit
}