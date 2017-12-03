package com.spotahome.dataEngTest

import twitter4j._
import twitter4j.auth.{AccessToken, OAuth2Token}
import twitter4j.conf.ConfigurationBuilder

object TwitterTrends extends App {


  private val builder: ConfigurationBuilder = new ConfigurationBuilder()
    .setApplicationOnlyAuthEnabled(true)

  val twitter4j: Twitter = new TwitterFactory(builder.build).getInstance

  twitter4j
    .setOAuthConsumer("tI5PKAnqfen0wzyCUN9ODquRQ", "FEaxuzWWD3AA0qwXNk9YqJSEgqH93kueKJh8tMou5L38wfZHmJ")

  val token: OAuth2Token = twitter4j.getOAuth2Token
  val accessToken = twitter4j.getOAuthAccessToken("tI5PKAnqfen0wzyCUN9ODquRQ", "FEaxuzWWD3AA0qwXNk9YqJSEgqH93kueKJh8tMou5L38wfZHmJ")

  val rateLimitStatus = twitter4j.getRateLimitStatus("search")
  val searchTweetsRateLimit = rateLimitStatus.get("/search/tweets")

  val twitterStream = new TwitterStreamFactory().getInstance(accessToken)

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
