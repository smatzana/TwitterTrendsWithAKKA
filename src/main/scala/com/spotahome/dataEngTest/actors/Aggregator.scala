package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, Props}

object Aggregator {

  def props = Props[Aggregator]

}

class Aggregator extends Actor {
  override def receive = ???
}
