package com.spotahome.dataEngTest.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import akka.pattern.ask
import com.spotahome.dataEngTest.TwitterTrends.AskPartialsSignal
import com.spotahome.dataEngTest.actors.Aggregator.PartialProcessed
import com.spotahome.dataEngTest.actors.PartialAggregator.{Hello, RegisterWithAggregator}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

object Aggregator {

  def props = Props[Aggregator]

  case class PartialProcessed()
}

class Aggregator extends Actor {

  implicit val timeout = Timeout(200 milliseconds)

  val partialAggregators = mutable.HashSet[ActorRef]()
  val previousResults = mutable.HashMap[String, Int]()

  def futureToFutureTry[T](f: Future[T]): Future[Try[T]] = {
      f.map(Success(_)).recover({case e => Failure(e)})
  }

  override def receive = {
    case RegisterWithAggregator() =>
      partialAggregators += sender()


    case AskPartialsSignal => {
      println(s"Agg got bvast to ask")
      val foo: mutable.Set[Future[Any]] = for (a <- partialAggregators) yield (a ? PartialProcessed)
      val goo: Future[mutable.Set[Try[Any]]] = Future.sequence(foo.map(futureToFutureTry)).map( _.filter(_.isSuccess))
      goo.map( set => set.foreach(tr => {println(s"Here's what I got ${tr.get}") ; tr.get}))
      goo.map( set => set.foreach(tr => tr match  {
        case Success(v) => {

        }
        case _ =>
      }))
    }
  }
}
