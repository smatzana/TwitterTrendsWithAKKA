package com.spotahome.dataEngTest.common

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
