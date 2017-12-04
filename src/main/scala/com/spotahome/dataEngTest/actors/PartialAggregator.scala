package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, Props}
import akka.routing.Broadcast
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import com.spotahome.dataEngTest.actors.PartialAggregator.HashTag

import scala.collection.immutable.{HashMap, TreeMap}
import scala.util.Success

object PartialAggregator {

  def props: Props = Props[PartialAggregator]

  case class HashTag(s: String)

  def hashMapping: ConsistentHashMapping = {
    case HashTag(key) => key.charAt(1).toString.toLowerCase
  }


}

class PartialAggregator extends Actor {

  import com.spotahome.dataEngTest.TwitterTrends.SendPartialsSignal

  type SortedMapKey = (String, Int)

  val tMap = TreeMap[Int, String]()
  var iTMap = HashMap[String, Int]()

  implicit val topHashTagsByCountOrdering: Ordering[(String, Int)] = Ordering.by[SortedMapKey, Int]( _._2)

  override def receive = {

    case HashTag(ht) => {
      println("Agg: Got the " + ht + " on actor "  + self )
      iTMap = iTMap.get(ht) match {
        case Some(currentCount) => iTMap + (ht -> (currentCount + 1))
        case None => iTMap + (ht -> 1)
      }
    }

    case SendPartialsSignal => {
      val t2: Map[SortedMapKey, Int] = TreeMap[SortedMapKey, Int]() ++ (for ((k, v) <- iTMap ) yield ((k,v), v))
      t2.foreach(t => println(s"${this} ${t}") )
    }
    case f => println("AG FAIL!!!"  + f)
  }
}
