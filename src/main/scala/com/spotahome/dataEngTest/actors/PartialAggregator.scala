package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, Props}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import com.spotahome.dataEngTest.actors.PartialAggregator.HashTag

object PartialAggregator {

  def props: Props = Props[PartialAggregator]

  case class HashTag(s: String)

  def hashMapping: ConsistentHashMapping = {
    case HashTag(key) => key.charAt(1)
  }

}

class PartialAggregator extends Actor {
  override def receive = {
    case HashTag(ht) => println("Agg: Got the " + ht)
    case _ => println("AG FAIL!!!")
  }
}
