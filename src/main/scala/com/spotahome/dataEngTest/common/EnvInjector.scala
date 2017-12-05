package com.spotahome.dataEngTest.common

object EnvInjector {

  val CsKey = "CONSUMER_KEY"
  val CsSecret = "CONSUMER_SECRET"
  val AccessToken = "ACCESS_TOKEN"
  val AccessTokenSecret = "ACCESS_TOKEN_SECRET"

  val envVars = List(CsKey, CsSecret, AccessToken, AccessTokenSecret)

  def getAllEnvVars = envVars.map(ev => ev -> sys.env.get(ev)).toMap

}
