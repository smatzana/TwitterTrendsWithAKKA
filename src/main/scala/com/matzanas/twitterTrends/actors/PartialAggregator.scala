package com.matzanas.twitterTrends.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping

import com.matzanas.twitterTrends.actors.Aggregator.PartialProcessed
import com.matzanas.twitterTrends.actors.PartialAggregator.{DeregisterWithAggregator, HashTag, RegisterWithAggregator}

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

  override def preStart() = {
    super.preStart()
    aggregator ! RegisterWithAggregator
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    aggregator ! DeregisterWithAggregator
    super.preRestart(reason, message)
  }

  override def receive = partials(Map())

  def partials(temporaryPartialAggregate: Map[String, Int]): Receive = {

    case HashTag(ht) =>
      temporaryPartialAggregate.get(ht) match {
        case Some(currentCount) => context become partials(temporaryPartialAggregate + (ht -> (currentCount + 1)))
        case None => context become partials(temporaryPartialAggregate + (ht -> 1))
      }


    case PartialProcessed => {
      sender ! temporaryPartialAggregate.toSeq.sortBy(-_._2).take(10)
      context become partials(Map())
    }

  }
}
