package it.murgia.antonio.sekai.base

case class Message[T](subject : String, body : T)