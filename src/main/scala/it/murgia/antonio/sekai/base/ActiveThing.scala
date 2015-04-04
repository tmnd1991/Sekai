package it.murgia.antonio.sekai.base

import scala.reflect.runtime.universe._
import it.murgia.antonio.sekai.base.CanReceiveResponse._

trait ActiveThing[I,O] extends Thing {
	environment.register(this)
  val inputType : Type
  val outputType : Type
  def receiveMessage(from : Identifier, message : Message[I]) : O
  def canReceiveMessage(from : Identifier, message : Message[I]) : CanReceiveResponse
}