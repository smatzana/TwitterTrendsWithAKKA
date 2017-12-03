package com.spotahome.dataEngTest

import twitter4j._
import twitter4j.auth.{AccessToken, OAuth2Token}
import twitter4j.conf.ConfigurationBuilder

object TwitterTrends extends App {


  /*private val builder: ConfigurationBuilder = new ConfigurationBuilder()
    .setApplicationOnlyAuthEnabled(true)

  val twitter4j: Twitter = new TwitterFactory(builder.build).getInstance

  twitter4j
    .setOAuthConsumer("tI5PKAnqfen0wzyCUN9ODquRQ", "FEaxuzWWD3AA0qwXNk9YqJSEgqH93kueKJh8tMou5L38wfZHmJ")

  val token: OAuth2Token = twitter4j.getOAuth2Token
  val accessToken = twitter4j.getOAuthAccessToken


  val rateLimitStatus = twitter4j.getRateLimitStatus("search")
  val searchTweetsRateLimit = rateLimitStatus.get("/search/tweets")*/


  var builder1 = new ConfigurationBuilder
  builder1.setDebugEnabled(true)
  builder1.setApplicationOnlyAuthEnabled(true)
  builder1.setOAuthConsumerKey("tI5PKAnqfen0wzyCUN9ODquRQ")
  builder1.setOAuthConsumerSecret("FEaxuzWWD3AA0qwXNk9YqJSEgqH93kueKJh8tMou5L38wfZHmJ")

  val token1 = new TwitterFactory(builder1.build).getInstance.getOAuth2Token

  val builder2 = new ConfigurationBuilder
  builder2.setDebugEnabled(true)
  builder2.setApplicationOnlyAuthEnabled(true)
  builder2.setOAuthConsumerKey("tI5PKAnqfen0wzyCUN9ODquRQ")
  builder2.setOAuthConsumerSecret("FEaxuzWWD3AA0qwXNk9YqJSEgqH93kueKJh8tMou5L38wfZHmJ")
  builder2.setOAuth2TokenType(token1.getTokenType)
  builder2.setOAuth2AccessToken(token1.getAccessToken)

  val twitterStream = new TwitterStreamFactory(builder2.build()).getInstance()

  twitterStream.addListener(
    new StatusListener () {
      override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = ()

      override def onScrubGeo(l: Long, l1: Long) = ()

      override def onStatus(status: Status) = println(s"${status.getText}")

      override def onTrackLimitationNotice(i: Int) = ()

      override def onStallWarning(stallWarning: StallWarning) = ()

      override def onException(e: Exception) = ()
    }
  )

  println(twitterStream.getAuthorization.isEnabled)
  twitterStream.filter(new FilterQuery("real madrid", "star wars"))
}
