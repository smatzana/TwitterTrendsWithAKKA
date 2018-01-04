package com.matzanas.twitterTrends.actors

import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import com.matzanas.twitterTrends.TwitterTrends.AskForPartials
import com.matzanas.twitterTrends.actors.Aggregator.PartialProcessed
import PartialAggregator.{DeregisterWithAggregator, RegisterWithAggregator}
import com.matzanas.twitterTrends.common.Coalesce
import com.matzanas.twitterTrends.common.TrendsPrettyPrint.PrettyPrint


object Aggregator {

  def props = Props[Aggregator]

  case class PartialProcessed()

}

class Aggregator extends Actor {

  private def liftToFutureTry[T](f: Future[T]): Future[Try[T]] =
    f.map(Success(_)).recover({ case e => Failure(e) })

  implicit val timeout = Timeout(200 milliseconds)

  override def receive = processPartials(HashSet(), List())

  def processPartials(partialAggregators: Set[ActorRef], previousResults: Iterable[(String, Int)]) : Receive = {

    case RegisterWithAggregator =>
      context become processPartials(partialAggregators + sender(), previousResults)

    case DeregisterWithAggregator =>
      context become processPartials(partialAggregators  - sender(), previousResults)

    case AskForPartials => {
      val setOfFutures = for (a <- partialAggregators) yield ask(a, PartialProcessed).mapTo[Seq[(String, Int)]]
      val futureOfSets = Future.sequence(setOfFutures.map(liftToFutureTry)).map(_.filter(_.isSuccess))

      futureOfSets
        .map(_.map(t => t.get))
        .andThen {
          case Success(e) => {
            val currentResults = e.foldLeft(ArrayBuffer[(String, Int)]())((agg, s) => agg ++= s).sortBy(-_._2).take(10)
            Coalesce.coalesceResults(currentResults, previousResults).prettyPrint
            context become processPartials(partialAggregators, currentResults)
          }
        }

    }
  }

}
