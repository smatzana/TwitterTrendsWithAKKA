package com.spotahome.dataEngTest.actors

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import com.spotahome.dataEngTest.TwitterTrends.AskForPartials
import com.spotahome.dataEngTest.actors.Aggregator.PartialProcessed
import com.spotahome.dataEngTest.actors.PartialAggregator.{DeregisterWithAggregator, RegisterWithAggregator}

object Aggregator {

  def props = Props[Aggregator]

  case class PartialProcessed()
}

class Aggregator extends Actor {

  private val partialAggregators = mutable.HashSet[ActorRef]()
  private val previousResults = ArrayBuffer[(String, Int)]()

  private def zipSeqWithIndex(s: Iterable[(String, Int)]) =
    s.map(t => t._1).zipWithIndex.toSeq.sortBy(_._2)

  private def coalesceResults(currentResults : Iterable[(String, Int)]) = {
    val previousIndices = zipSeqWithIndex(previousResults).toMap
    previousResults.clear()
    previousResults ++= currentResults

    val j: Map[String, Int] = currentResults.map(t => t._1 -> t._2).toMap
    (for ( (k, i) <- zipSeqWithIndex(currentResults)) yield {
      previousIndices.get(k) match {
        case Some(index) if index < i => (k, j.get(k).get, s"\u2193 (${Math.abs(i - index)} positions)")
        case Some(index) if index > i => (k, j.get(k).get, s"\u2191 (${Math.abs(i - index)} positions)")
        case Some(_)  => (k, j.get(k).get, s"=")
        case None => (k, j.get(k).get, "new")
      }
    }).sortBy(- _._2)
  }


  private def liftToFutureTry[T](f: Future[T]): Future[Try[T]] =
    f.map(Success(_)).recover({case e => Failure(e)})

  implicit val timeout = Timeout(200 milliseconds)

  import com.spotahome.dataEngTest.common.TrendsPrettyPrint.PrettyPrint

  override def receive = {

    case RegisterWithAggregator =>
      partialAggregators += sender()

    case DeregisterWithAggregator =>
      partialAggregators -= sender()

    case AskForPartials => {
      var currentResults = ArrayBuffer[(String, Int)]()
      val setOfFutures = for (a <- partialAggregators) yield ask(a, PartialProcessed).mapTo[Seq[(String, Int)]]
      val futureOfSets = Future.sequence(setOfFutures.map(liftToFutureTry)).map( _.filter(_.isSuccess))
      futureOfSets.map((set: mutable.Set[Try[Seq[(String, Int)]]]) => set.foreach(tr => {
        currentResults ++= tr.get
      })).andThen {
        case _ => coalesceResults(currentResults.sortBy(- _._2).take(10)).prettyPrint.foreach(println)
      }

    }
  }
}
