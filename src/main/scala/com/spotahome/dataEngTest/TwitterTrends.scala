package com.spotahome.dataEngTest

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.spotahome.dataEngTest.actors.{Parser, TwitterSearcher}

object TwitterTrends extends App {

  val system: ActorSystem = ActorSystem("Spotahome-Data-Engineering-Test")

  val parserRouter: ActorRef =
    system.actorOf(RoundRobinPool(5).props(Props[Parser]), "parserRouter")

  val searcher = system.actorOf(TwitterSearcher.props(parserRouter), "twitterSearcher")

  searcher ! TwitterSearcher.StartTwitterSearch
}
