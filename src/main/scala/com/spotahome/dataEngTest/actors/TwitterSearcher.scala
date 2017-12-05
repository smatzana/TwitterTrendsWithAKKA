package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, ActorRef, Props}

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{FilterQuery, TwitterStreamFactory}

import com.spotahome.dataEngTest.actors.TwitterSearcher.StartTwitterSearch
import com.spotahome.dataEngTest.common.EnvInjector

object TwitterSearcher {

  def props(parserRouter: ActorRef, envVars: Map[String, String]) = Props(new TwitterSearcher(parserRouter, envVars))

  case class StartTwitterSearch()

}

class TwitterSearcher(parserActor: ActorRef, envVars: Map[String, String]) extends Actor {

  private val twitterStream = new TwitterStreamFactory(
    new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(envVars.get(EnvInjector.csKey).get)
      .setOAuthConsumerSecret(envVars.get(EnvInjector.csSecret).get)
      .setOAuthAccessToken(envVars.get(EnvInjector.accessToken).get)
      .setOAuthAccessTokenSecret(envVars.get(EnvInjector.accessTokenSecret).get).build()).getInstance()

  private def prepare = {
    twitterStream.onStatus(parserActor ! Parser.ParseStatus(_))
    twitterStream.onException(_ => context.system.terminate())
    twitterStream.filter((new FilterQuery).track("real madrid", "star wars", "justin bieber"))
  }

  override def receive = {
    case StartTwitterSearch => prepare
  }
}
