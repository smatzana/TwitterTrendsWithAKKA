package com.spotahome.dataEngTest.common

object Coalesce {

  private def zipSeqWithIndex(s: Iterable[(String, Int)]) =
    s.map(t => t._1).zipWithIndex.toSeq.sortBy(_._2)

  def coalesceResults(currentResults: Iterable[(String, Int)], previousResults: Iterable[(String, Int)]): (Seq[(String, Int, String)], Iterable[(String, Int)]) = {

    val previousIndices = zipSeqWithIndex(previousResults).toMap
    val resultLookup = currentResults.map(t => t._1 -> t._2).toMap

    (
      (for ((k, i) <- zipSeqWithIndex(currentResults)) yield {
        previousIndices.get(k) match {
          case Some(index) if index < i => (k, resultLookup.get(k).get, s"\u2193 (${Math.abs(i - index)} positions)")
          case Some(index) if index > i => (k, resultLookup.get(k).get, s"\u2191 (${Math.abs(i - index)} positions)")
          case Some(_) => (k, resultLookup.get(k).get, s"=")
          case None => (k, resultLookup.get(k).get, "new")
        }
      }).sortBy(-_._2)
      , currentResults)
  }
}
