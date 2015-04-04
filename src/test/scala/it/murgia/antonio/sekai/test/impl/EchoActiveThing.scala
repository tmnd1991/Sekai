package it.murgia.antonio.sekai.test.impl

import it.murgia.antonio.sekai.base.CanReceiveResponse._
import it.murgia.antonio.sekai.base.Environments.Environment
import it.murgia.antonio.sekai.base._

import scala.reflect.runtime.universe._

class EchoActiveThing(implicit env: Environment) extends Thing("Echo")(env) with ActiveThing[String,String] {

  override val outputType = typeOf[String]
  override val inputType  = typeOf[String]

  override def receiveMessage(from: Identifier, message: Message[String]): String = message.body


  override def canReceiveMessage(from: Identifier, message: Message[String]): CanReceiveResponse = YES
}