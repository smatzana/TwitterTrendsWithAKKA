package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import com.spotahome.dataEngTest.actors.Aggregator.PartialProcessed
import com.spotahome.dataEngTest.actors.PartialAggregator.{HashTag, Hello, RegisterWithAggregator}

import scala.collection.mutable.HashMap

object PartialAggregator {

  def props(aggregator: ActorRef): Props = Props(new PartialAggregator(aggregator))

  case class HashTag(s: String)
  case class RegisterWithAggregator()
  case class Hello()

  def hashMapping: ConsistentHashMapping = {
    case HashTag(key) => key.charAt(1).toString.toLowerCase
  }

}

class PartialAggregator(aggregator: ActorRef) extends Actor {

  type SortedMapKey = (String, Int)

  val iTMap = HashMap[String, Int]()

  aggregator ! RegisterWithAggregator()

  override def receive = {

    case Hello => {
      println("Got my hello")
    }
    case HashTag(ht) =>
      iTMap.get(ht) match {
        case Some(currentCount) => iTMap += (ht -> (currentCount + 1))
        case None => iTMap += (ht -> 1)
      }


    case PartialProcessed => {
      println(s"got ASKED!! ${iTMap.toSeq.sortBy(-_._2).take(10)}")
      sender ! iTMap.toSeq.sortBy(-_._2).take(10)
      iTMap.clear()
    }

  }
}
