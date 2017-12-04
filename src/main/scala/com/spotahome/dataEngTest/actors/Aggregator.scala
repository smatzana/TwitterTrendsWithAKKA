package com.spotahome.dataEngTest.actors

import scala.collection.mutable.ArrayBuffer
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
import com.spotahome.dataEngTest.utils.Utilities

object Aggregator {

  def props = Props[Aggregator]

  case class PartialProcessed()
}

class Aggregator extends Actor {

  implicit val timeout = Timeout(200 milliseconds)

  private val partialAggregators = mutable.HashSet[ActorRef]()
  private val previousResults = mutable.Set[(String, Int)]()

  private def futureToFutureTry[T](f: Future[T]): Future[Try[T]] = {
      f.map(Success(_)).recover({case e => Failure(e)})
  }

  def zipSeqWithIndex(s: Iterable[(String, Int)]): Map[String, Int] = (for (e <- s) yield e._1).zipWithIndex.toMap

  private def coalesceResults(currentResults : Iterable[(String, Int)]): immutable.Iterable[(String, Int, String)] = {
    val previousIndices = zipSeqWithIndex(previousResults)
    previousResults.clear()
    previousResults ++= currentResults
    val j = currentResults.toMap
    for ( (k, i) <- zipSeqWithIndex(currentResults)) yield {
      previousIndices.get(k) match {
        case Some(index) if index < i => (k, j.get(k).get, s"\u2193 (${Math.abs(i - index)} positions")
        case Some(index) if index > i => (k, j.get(k).get, s"\u2191 (${Math.abs(i - index)} positions")
        case Some(_)  => (k, j.get(k).get, s"=")
        case None => (k, j.get(k).get, "new")
      }
    }

  }

  override def receive = {
    case RegisterWithAggregator() =>
      partialAggregators += sender()


    case AskPartialsSignal => {
      var currentResults = ArrayBuffer[(String, Int)]()
      val foo = for (a <- partialAggregators) yield ask(a, PartialProcessed).mapTo[Seq[(String, Int)]]
      val goo: Future[mutable.HashSet[Try[Seq[(String, Int)]]]] = Future.sequence(foo.map(futureToFutureTry)).map( _.filter(_.isSuccess))
      goo.map((set: mutable.Set[Try[Seq[(String, Int)]]]) => set.foreach(tr => {
        currentResults ++= tr.get
      })).andThen(  {
        case _ => {
          println(s"current ${currentResults}")
          println(s"Is this my total? ${currentResults.sortBy(- _._2).take(10)}")
          //val res = coalesceResults(Utilities.convertMapToSortedSeqByValue(currentResults.toMap).take(10))
          println("COALESCED")
          //res.foreach(println)
        }
      })

    }
  }
}
