package com.spotahome.dataEngTest

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.{BalancingPool, ConsistentHashingPool, RoundRobinPool, SmallestMailboxPool}
import com.spotahome.dataEngTest.actors.{Parser, PartialAggregator, TwitterSearcher}

object TwitterTrends extends App {

  val system: ActorSystem = ActorSystem("Spotahome-Data-Engineering-Test")

  val partialAggregator =
    system.actorOf(ConsistentHashingPool(10, hashMapping = PartialAggregator.hashMapping).
      props(Props[PartialAggregator]), name = "partialAggregator")

  val parserRouter =
    system.actorOf(SmallestMailboxPool(5).props(Parser.props(partialAggregator)), "parserRouter")

  val searcher = system.actorOf(TwitterSearcher.props(parserRouter), "twitterSearcher")

  searcher ! TwitterSearcher.StartTwitterSearch
}
