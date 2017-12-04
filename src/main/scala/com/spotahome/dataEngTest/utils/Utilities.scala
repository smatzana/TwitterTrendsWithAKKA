package com.spotahome.dataEngTest.utils

import scala.collection.mutable.TreeMap

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

  convertMapToSortedSeqByValue( Map("#justinbieber" -> 1, "#mtvhottest" -> 14,
    "#other" -> 1, "#records" -> 1, "#retweet" -> 1, "#top50nomino" -> 1, "#vinyl" -> 1))

}
