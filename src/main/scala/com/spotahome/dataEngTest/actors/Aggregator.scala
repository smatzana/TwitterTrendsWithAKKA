package com.spotahome.dataEngTest.actors

import scala.collection.{immutable, mutable}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import com.spotahome.dataEngTest.TwitterTrends.AskPartialsSignal
import com.spotahome.dataEngTest.actors.Aggregator.PartialProcessed
import com.spotahome.dataEngTest.actors.PartialAggregator.RegisterWithAggregator

object Aggregator {

  def props = Props[Aggregator]

  case class PartialProcessed()
}

class Aggregator extends Actor {

  implicit val timeout = Timeout(200 milliseconds)

  private val partialAggregators = mutable.HashSet[ActorRef]()
  private val previousResults = mutable.HashMap[String, Int]()

  private def futureToFutureTry[T](f: Future[T]): Future[Try[T]] = {
      f.map(Success(_)).recover({case e => Failure(e)})
  }

  def zipMapWithIndex(m: Map[String, Int]): Map[String, Int] = (for ((k, _) <- m) yield k).zipWithIndex.toMap

  private def coalesceResults(currentResults : Map[String, Int]): immutable.Iterable[(String, Int, String)] = {
    val previousIndices = this.zipMapWithIndex(previousResults.toMap)
    previousResults.clear()
    previousResults ++= currentResults
    for ( (k, i) <- zipMapWithIndex(currentResults)) yield {
      previousIndices.get(k) match {
        case Some(index) if index < i => (k, currentResults.get(k).get, s"\u2193 (${Math.abs(i - index)} positions")
        case Some(index) if index > i => (k, currentResults.get(k).get, s"\u2191 (${Math.abs(i - index)} positions")
        case Some(_)  => (k, currentResults.get(k).get, s"=")
        case None => (k, currentResults.get(k).get, "new")
      }
    }

  }

  override def receive = {
    case RegisterWithAggregator() =>
      partialAggregators += sender()


    case AskPartialsSignal => {
      val currentResults = mutable.HashMap[String, Int]()
      val foo = for (a <- partialAggregators) yield ask(a, PartialProcessed).mapTo[Map[String, Int]]
      val goo: Future[mutable.HashSet[Try[Map[String, Int]]]] = Future.sequence(foo.map(futureToFutureTry)).map( _.filter(_.isSuccess))
      goo.map((set: mutable.Set[Try[Map[String, Int]]]) => set.foreach(tr => {
        currentResults ++= tr.get
      })).andThen(  {
        case _ => {
          println(s"Is this my total? ${currentResults.toSeq.sortBy(-_._2).take(10)}")
          val res = coalesceResults(currentResults.toSeq.sortBy(-_._2).take(10).toMap)
          println("COALESCED")
          res.foreach(println)
        }
      })

    }
  }
}
