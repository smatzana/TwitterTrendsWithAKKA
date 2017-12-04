package com.spotahome.dataEngTest

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorRef, ActorSelection, ActorSystem, Props}
import akka.routing._
import com.spotahome.dataEngTest.actors.{Parser, PartialAggregator, TwitterSearcher}

object TwitterTrends extends App {

  val SystemName = "Spotahome-Data-Engineering-Test"
  val SendPartialsSignal = "send partials"

  val system: ActorSystem = ActorSystem(SystemName)

  val partialAggregator =
    system.actorOf(ConsistentHashingPool(1, hashMapping = PartialAggregator.hashMapping).
      props(Props[PartialAggregator]), name = "partialAggregator")

  val parserRouter =
    system.actorOf(SmallestMailboxPool(5).props(Parser.props(partialAggregator)), "parserRouter")

  val searcher = system.actorOf(TwitterSearcher.props(parserRouter), "twitterSearcher")

  system.scheduler.schedule(10 seconds, 10 seconds, partialAggregator, Broadcast(SendPartialsSignal))
  //router ! Broadcast("Watch out for Davy Jones' locker")
  searcher ! TwitterSearcher.StartTwitterSearch
}
