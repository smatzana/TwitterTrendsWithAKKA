package com.spotahome.dataEngTest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.routing._

import com.spotahome.dataEngTest.actors._
import com.spotahome.dataEngTest.common.EnvInjector

object TwitterTrends extends App {

  val envVars  = EnvInjector.getAllEnvVars.map(
     _ match {
       case (ev, None) => throw new RuntimeException(s"Missing env var ${ev}")
       case (ev, Some(f)) => (ev, f)
     }
  )
  val SystemName = "SpotAHome-Data-Engineering-Test"
  val AskForPartials = "ask partials"

  val system: ActorSystem = ActorSystem(SystemName)

  val aggregator = system.actorOf(Aggregator.props, name = "aggregator")

  val partialAggregator =
    system.actorOf(ConsistentHashingPool(30, hashMapping = PartialAggregator.hashMapping).
      props(PartialAggregator.props(aggregator)), name = "partialAggregator")

  val parserRouter =
    system.actorOf(SmallestMailboxPool(10).props(Parser.props(partialAggregator)), "parserRouter")

  val searcher = system.actorOf(TwitterSearcher.props(parserRouter, envVars), "twitterSearcher")

  system.scheduler.schedule(10 seconds, 10 seconds, aggregator, AskForPartials)

  searcher ! TwitterSearcher.StartTwitterSearch
}
