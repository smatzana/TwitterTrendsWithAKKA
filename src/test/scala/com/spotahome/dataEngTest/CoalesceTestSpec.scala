package com.spotahome.dataEngTest

import scala.collection.mutable.ArrayBuffer

import com.spotahome.dataEngTest.common._
import org.scalatest._

class CoalesceTestSpec extends FlatSpec with Matchers {

  "Mark trend progression correctly" should "work" in {
    val previousResults = ArrayBuffer[(String, Int)]()

    var cr1 = ArrayBuffer(("#starwars",3), ("#losultimosjedi",2), ("#swipe4thenextsong",1), ("#mtvhottest",1),
      ("#electronicarts",1), ("#lucasfilm",1), ("#jedi",1), ("#offroad",1), ("#gmc",1), ("#daisyridley",1))

    val (trends, previous) = Coalesce.coalesceResults(cr1, previousResults)

    trends.forall(_._3 == New()) should be(true)

    previousResults ++= previous

    val cr2 = ArrayBuffer(("#mtvhottest",5), ("#starwars",3), ("#scifi",1), ("#madrid",1),
      ("#daisyridley",1), ("#losultimosjedi",1), ("#jedi",1))

    val (trends2, _) = Coalesce.coalesceResults(cr2, previousResults)

    trends2(0)._1 should be("#mtvhottest")
    trends2(0)._3 should be(Up(3))
    trends2(1)._1 should be("#starwars")
    trends2(1)._3 should be(Down(1))
    trends2(2)._1 should be("#scifi")
    trends2(2)._3 should be(New())
    trends2(6)._1 should be("#jedi")
    trends2(6)._3 should be(Same())
  }

}
