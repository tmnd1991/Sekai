package it.murgia.antonio.sekai

package object base {
  type Identifier = String
  val NoOne : Identifier = ":__:__:__*__*__*"
  object CanReceiveResponse extends Enumeration {
    type CanReceiveResponse = Value
    val YES, NO, DONTCONSUME = Value
  }

}