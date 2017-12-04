package com.spotahome.dataEngTest.actors

import scala.collection.mutable.HashMap

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping

import com.spotahome.dataEngTest.actors.Aggregator.PartialProcessed
import com.spotahome.dataEngTest.actors.PartialAggregator.{DeregisterWithAggregator, HashTag, RegisterWithAggregator}

object PartialAggregator {

  def props(aggregator: ActorRef): Props = Props(new PartialAggregator(aggregator))

  case class HashTag(s: String)
  case class RegisterWithAggregator()
  case class DeregisterWithAggregator()
  case class Hello()

  def hashMapping: ConsistentHashMapping = {
    case HashTag(key) => key.charAt(1).toString.toLowerCase
  }

}

class PartialAggregator(aggregator: ActorRef) extends Actor {

  type SortedMapKey = (String, Int)

  val iTMap = HashMap[String, Int]()

  override def preStart() = {
    super.preStart()
    aggregator ! RegisterWithAggregator
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preStart()
    aggregator ! DeregisterWithAggregator
  }

  override def receive = {

    case HashTag(ht) =>
      iTMap.get(ht) match {
        case Some(currentCount) => iTMap += (ht -> (currentCount + 1))
        case None => iTMap += (ht -> 1)
      }


    case PartialProcessed => {
      sender ! iTMap.toSeq.sortBy(-_._2).take(10)
      iTMap.clear()
    }

  }
}
