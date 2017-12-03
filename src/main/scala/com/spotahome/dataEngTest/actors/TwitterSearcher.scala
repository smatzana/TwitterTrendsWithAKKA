package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, Props}
import com.spotahome.dataEngTest.actors.TwitterSearcher.StartTwitterSearch

object TwitterSearcher {

  def props: Props = Props[TwitterSearcher]

  case class StartTwitterSearch()

}

class TwitterSearcher extends Actor {
  override def receive = {
    case StartTwitterSearch =>
  }
}
