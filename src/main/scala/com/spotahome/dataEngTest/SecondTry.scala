package com.spotahome.dataEngTest

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{StallWarning, Status, StatusDeletionNotice, StatusListener}

object SecondTry extends App {

  var builder2 = new ConfigurationBuilder
  builder2.setDebugEnabled(true)
  builder2.setOAuthConsumerKey("UAROc811Qme3SSnipnEcNplth")
  builder2.setOAuthConsumerSecret("DIfRdcJtFaEMEDVmbtjLvcfF82WcHoB2yLerbzUKT6NyXMqXhm")
  builder2.setOAuthAccessToken("937335837146140673-xDou2Ehe5xtTFTF9Ah8V5NxDx06Vtco")
  builder2.setOAuthAccessTokenSecret("Z6LDFZyWoYEZRbyZX7tzq2vg8wbI3r4vOuAG7AaFEs7k5")

  import twitter4j.TwitterStreamFactory

  val twitterStream = new TwitterStreamFactory(builder2.build).getInstance

  val listener = new StatusListener() {
    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = ???

    override def onScrubGeo(l: Long, l1: Long) = ???

    override def onStatus(status: Status) = println(s"${status.getText}")

    override def onTrackLimitationNotice(i: Int) = ???

    override def onStallWarning(stallWarning: StallWarning) = ???

    override def onException(e: Exception) = println(e)
  }

  import twitter4j.FilterQuery

  val fq = new FilterQuery

  fq.track("real madrid", "star wars")

  twitterStream.addListener(listener)
  twitterStream.filter(fq)
}
