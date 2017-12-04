package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, ActorRef, Props}

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{FilterQuery, TwitterStreamFactory}

import com.spotahome.dataEngTest.actors.TwitterSearcher.StartTwitterSearch

object TwitterSearcher {

  def props(parserActor: ActorRef): Props = Props(new TwitterSearcher(parserActor))

  case class StartTwitterSearch()

}

class TwitterSearcher(parserActor: ActorRef) extends Actor {

  var twitterStream = new TwitterStreamFactory(
    new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey("UAROc811Qme3SSnipnEcNplth")
      .setOAuthConsumerSecret("DIfRdcJtFaEMEDVmbtjLvcfF82WcHoB2yLerbzUKT6NyXMqXhm")
      .setOAuthAccessToken("937335837146140673-xDou2Ehe5xtTFTF9Ah8V5NxDx06Vtco")
      .setOAuthAccessTokenSecret("Z6LDFZyWoYEZRbyZX7tzq2vg8wbI3r4vOuAG7AaFEs7k5").build()).getInstance()

  def prepare = {
    twitterStream.onStatus(parserActor ! Parser.ParseStatus(_))
    twitterStream.filter((new FilterQuery).track("real madrid", "star wars", "justin bieber"))
  }

  override def receive = {
    case StartTwitterSearch => prepare
  }
}
