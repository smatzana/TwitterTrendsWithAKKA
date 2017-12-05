package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, ActorRef, Props}

import twitter4j.Status

import com.spotahome.dataEngTest.actors.Parser.ParseStatus
import com.spotahome.dataEngTest.actors.PartialAggregator.HashTag

object Parser {
  def props(partialAggregator: ActorRef) = Props(new Parser(partialAggregator))

  case class ParseStatus(s: Status)
}

class Parser(partialAggregator: ActorRef)  extends Actor {

  override def receive =  {

    case ParseStatus(s) =>
      "(?<!\\w)#\\w+".r
        .findAllIn(s.getText)
        .foreach(s => partialAggregator ! HashTag(s.toLowerCase()))

    case _ =>
  }
}
