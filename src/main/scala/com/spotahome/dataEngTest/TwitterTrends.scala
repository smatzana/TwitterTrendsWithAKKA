package com.spotahome.dataEngTest

import twitter4j._
import twitter4j.auth.{AccessToken, OAuth2Token}
import twitter4j.conf.ConfigurationBuilder

object TwitterTrends extends App {

  var builder1 = new ConfigurationBuilder
  builder1.setDebugEnabled(true)
  builder1.setApplicationOnlyAuthEnabled(true)
  builder1.setOAuthConsumerKey("3rJOl1ODzm9yZy63FACdg")
  builder1.setOAuthConsumerSecret("5jPoQ5kQvMJFDYRNE8bQ4rHuds4xJqhvgNJM4awaE8")

  val token1 = new TwitterFactory(builder1.build).getInstance.getOAuth2Token

  var builder2 = new ConfigurationBuilder
  builder2.setDebugEnabled(true)
  builder2.setApplicationOnlyAuthEnabled(true)
  builder2.setOAuthConsumerKey("3rJOl1ODzm9yZy63FACdg")
  builder2.setOAuthConsumerSecret("5jPoQ5kQvMJFDYRNE8bQ4rHuds4xJqhvgNJM4awaE8")
  builder2.setOAuth2TokenType(token1.getTokenType)
  builder2.setOAuth2AccessToken(token1.getAccessToken)

  import twitter4j.Twitter
  import twitter4j.TwitterFactory

  val twitter = new TwitterFactory(builder2.build).getInstance

  val query = new Query("Korea")
  query.setCount(1)
  val result = twitter.search(query);

  val foo = result.getTweets.forEach(s => println(s.getText))

  builder2 = new ConfigurationBuilder
  builder2.setDebugEnabled(true)
  builder2.setApplicationOnlyAuthEnabled(true)
  builder2.setOAuthConsumerKey("3rJOl1ODzm9yZy63FACdg")
  builder2.setOAuthConsumerSecret("5jPoQ5kQvMJFDYRNE8bQ4rHuds4xJqhvgNJM4awaE8")
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
