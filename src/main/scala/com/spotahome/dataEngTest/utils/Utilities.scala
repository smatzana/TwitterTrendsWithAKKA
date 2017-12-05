package com.spotahome.dataEngTest.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import scala.collection.mutable.{ArrayBuffer, TreeMap}

object Utilities extends App {

  implicit object ValOrdering extends Ordering[(String, Int)] {
    override def compare(x: (String, Int), y: (String, Int)): Int = -(x._2 compareTo(y._2))
  }

  def convertMapToSortedSeqByValue(m: Map[String, Int]) = {
    val foo = TreeMap[(String, Int), Int]()
    val tst: Map[(String, Int), Int] = (for ((k, v) <- m ) yield ((k, v), 1))
    tst.keys.foreach(println)
    //val foo = TreeMap[(String, Int), Int](tst.toSeq)

    //foo += (for ( (k, v) <- m ) yield ((k, v), 1))
    //foo.keys
  }

  /*convertMapToSortedSeqByValue( Map("#justinbieber" -> 1, "#mtvhottest" -> 14,
    "#other" -> 1, "#records" -> 1, "#retweet" -> 1, "#top50nomino" -> 1, "#vinyl" -> 1))*/

  private val previousResults = ArrayBuffer[(String, Int)]()

  def zipSeqWithIndex(s: Iterable[(String, Int)]): Seq[(String, Int)] = {
    s.map(t => t._1).zipWithIndex.toSeq.sortBy(_._2)
  }

  def coalesceResults(currentResults : Iterable[(String, Int)]): Seq[(String, Int, String)] = {
    val previousIndices = zipSeqWithIndex(previousResults).toMap
    previousResults.clear()
    previousResults ++= currentResults

    val j: Map[String, Int] = currentResults.map(t => t._1 -> t._2).toMap
    (for ( (k, i) <- zipSeqWithIndex(currentResults)) yield {
      println(s"key ${k} new index: ${i}, old index was ${previousIndices.get(k)}")
      previousIndices.get(k) match {
        case Some(index) if index < i => (k, j.get(k).get, s"\u2193 (${Math.abs(i - index)} positions)")
        case Some(index) if index > i => (k, j.get(k).get, s"\u2191 (${Math.abs(i - index)} positions)")
        case Some(_)  => (k, j.get(k).get, s"=")
        case None => (k, j.get(k).get, "new")
      }
    }).sortBy(- _._2)
  }

  private val dateFormat =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def prettyPrint(results: Seq[(String, Int, String)]) = {
    if (results.nonEmpty) {
      val time = LocalDateTime.now()
      println(s"Tweets from ${time.minus(10, ChronoUnit.SECONDS).format(dateFormat)} to ${time.format(dateFormat)}}")
    }
  }

  /*var currentResults = ArrayBuffer[(String, Int)]("#justinbieber" -> 1, "#mtvhottest" -> 14,
    "#other" -> 1, "#records" -> 1, "#retweet" -> 1, "#top50nomino" -> 1, "#vinyl" -> 1)
  val r1 = coalesceResults(currentResults.sortBy(- _._2))
  println("----A----")
  r1.foreach(println)
  currentResults = ArrayBuffer[(String, Int)]("#justinbieber" -> 5, "#mtvhottest" -> 14,
    "#other" -> 1, "#records" -> 3, "#retweet" -> 1, "#top50nomino" -> 6, "#vinyl" -> 1)
  val r2 = coalesceResults(currentResults.sortBy(- _._2))
  println("----b----")
  r2.foreach(println)*/

  var cr1 = ArrayBuffer(("#starwars",3), ("#losultimosjedi",2), ("#swipe4thenextsong",1), ("#mtvhottest",1),
    ("#electronicarts",1), ("#lucasfilm",1), ("#jedi",1), ("#offroad",1), ("#gmc",1), ("#daisyridley...",1))
  val r3 = coalesceResults(cr1.sortBy(- _._2))

  val cr4 = ArrayBuffer(("#mtvhottest",5), ("#starwars",3), ("#scifi",1), ("#madrid",1),
    ("#daisyridley...",1), ("#losultimosjedi",1), ("#jedi",1))
  val r4 = coalesceResults(cr4.sortBy(- _._2))

  prettyPrint(coalesceResults(cr4.sortBy(- _._2)))
}
