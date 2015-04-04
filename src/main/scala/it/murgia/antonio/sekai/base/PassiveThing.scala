package it.murgia.antonio.sekai.base

trait PassiveThing extends Thing{
  environment.register(this)
}