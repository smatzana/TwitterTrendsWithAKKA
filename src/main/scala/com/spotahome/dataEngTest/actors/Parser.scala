package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, Props}
import com.spotahome.dataEngTest.actors.Parser.ParseStatus
import twitter4j.Status

object Parser {
  def props: Props = Props[Parser]

  case class ParseStatus(s: Status)
}

class Parser extends Actor {
  override def receive =  {
    case ParseStatus(s) => println(s"Got a status ${s.getText}")
    case _ => println("Fails")
  }
}
