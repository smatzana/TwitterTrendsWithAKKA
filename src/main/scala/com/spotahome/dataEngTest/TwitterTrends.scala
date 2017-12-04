package com.spotahome.dataEngTest

import akka.actor.ActorSystem
import akka.routing._
import com.spotahome.dataEngTest.actors._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object TwitterTrends extends App {

  val SystemName = "Spotahome-Data-Engineering-Test"
  val AskPartialsSignal = "ask partials"

  val system: ActorSystem = ActorSystem(SystemName)

  val aggregator = system.actorOf(Aggregator.props, name = "aggregator")

  val partialAggregator =
    system.actorOf(ConsistentHashingPool(1, hashMapping = PartialAggregator.hashMapping).
      props(PartialAggregator.props(aggregator)), name = "partialAggregator")

  val parserRouter =
    system.actorOf(SmallestMailboxPool(5).props(Parser.props(partialAggregator)), "parserRouter")

  val searcher = system.actorOf(TwitterSearcher.props(parserRouter), "twitterSearcher")

  system.scheduler.schedule(10 seconds, 10 seconds, aggregator, AskPartialsSignal)
  searcher ! TwitterSearcher.StartTwitterSearch
}
