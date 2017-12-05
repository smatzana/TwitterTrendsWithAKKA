package com.spotahome.dataEngTest.common

object EnvInjector {

  val csKey = "CONSUMER_KEY"
  val csSecret = "CONSUMER_SECRET"
  val accessToken = "ACCESS_TOKEN"
  val accessTokenSecret = "ACCESS_TOKEN_SECRET"

  val envVars = List(csKey, csSecret, accessToken, accessTokenSecret)

  def getAllEnvVars = envVars.map(ev => ev -> sys.env.get(ev)).toMap

}
