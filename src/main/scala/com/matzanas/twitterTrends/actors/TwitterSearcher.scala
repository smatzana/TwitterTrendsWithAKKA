package com.matzanas.twitterTrends.actors

import akka.actor.{Actor, ActorRef, Props}

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{FilterQuery, TwitterStreamFactory}

import com.matzanas.twitterTrends.actors.TwitterSearcher.StartTwitterSearch
import com.matzanas.twitterTrends.common.EnvInjector

object TwitterSearcher {

  def props(parserRouter: ActorRef, envVars: Map[String, String]) = Props(new TwitterSearcher(parserRouter, envVars))

  case class StartTwitterSearch()

}

class TwitterSearcher(parserActor: ActorRef, envVars: Map[String, String]) extends Actor {

  private val twitterStream = new TwitterStreamFactory(
    new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(envVars.get(EnvInjector.CsKey).get)
      .setOAuthConsumerSecret(envVars.get(EnvInjector.CsSecret).get)
      .setOAuthAccessToken(envVars.get(EnvInjector.AccessToken).get)
      .setOAuthAccessTokenSecret(envVars.get(EnvInjector.AccessTokenSecret).get).build()).getInstance()

  override def receive = {
    case StartTwitterSearch => {
      twitterStream.onStatus(parserActor ! Parser.ParseStatus(_))
      twitterStream.onException(_ => context.system.terminate())
      twitterStream.filter((new FilterQuery).track("real madrid", "star wars", "justin bieber"))
    }
  }
}
