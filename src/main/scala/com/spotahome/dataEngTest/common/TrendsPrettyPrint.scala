package com.spotahome.dataEngTest.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TrendsPrettyPrint {

  implicit class PrettyPrint(results: Seq[(String, Int, String)]) {

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val lineSep = ((1 to 60).map( _ => "-")).mkString

    def prettyPrint() = {

      val time = LocalDateTime.now()
      val headline = s"Tweets from ${time.minus(10, ChronoUnit.SECONDS).format(dateFormat)} to ${time.format(dateFormat)}"

      results match {
        case Nil => lineSep :: Nil
        case _ => headline +:
          lineSep +:
          results.map(r => f"${results.indexOf(r) + 1}%3s | ${r._1}%-40s | ${r._2}%4s | ${r._3}") :+
          lineSep
      }
    }

  }

}
