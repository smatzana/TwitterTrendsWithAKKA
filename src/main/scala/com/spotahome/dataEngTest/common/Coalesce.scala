package com.spotahome.dataEngTest.common

object Coalesce {

  trait Position
  case class Up(i: Int) extends Position {
    override def toString: String = s"\u2191 (${i} positions)"
  }
  case class Down(i: Int) extends Position {
    override def toString: String = s"\u2193 (${i} positions)"
  }
  case class New() extends Position {
    override def toString: String = s"new"
  }
  case class Same() extends Position {
    override def toString: String = s"="
  }

  private def zipSeqWithIndex(s: Iterable[(String, Int)]) =
    s.map(t => t._1).zipWithIndex.toSeq.sortBy(_._2)

  def coalesceResults(currentResults: Iterable[(String, Int)], previousResults: Iterable[(String, Int)]): (Seq[(String, Int, Position)], Iterable[(String, Int)]) = {

    val previousIndices = zipSeqWithIndex(previousResults).toMap
    val resultLookup = currentResults.map(t => t._1 -> t._2).toMap

    (
      (for ((k, i) <- zipSeqWithIndex(currentResults)) yield {
        previousIndices.get(k) match {
          case Some(index) if index < i => (k, resultLookup.get(k).get, Down(Math.abs(i - index)))
          case Some(index) if index > i => (k, resultLookup.get(k).get, Up(Math.abs(i - index)))
          case Some(_) => (k, resultLookup.get(k).get, Same())
          case None => (k, resultLookup.get(k).get, New())
        }
      }).sortBy(-_._2)
      , currentResults)
  }
}
