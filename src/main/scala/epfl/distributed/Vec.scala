package epfl.distributed

import spire.math._

import scala.collection.{IndexedSeqOptimized, mutable}

case class Vec(v: IndexedSeq[Number]) extends IndexedSeqOptimized[Number, IndexedSeq[Number]] {
  require(v.nonEmpty, "A vector cannot be empty")

  def elementWiseOp(other: Vec, op: (Number, Number) => Number): Vec = {
    require(other.length == length, "Can't perform element-wise operation on vectors of differnet length")
    Vec(
      v zip other.v map {
        case (e1, e2) => op(e1, e2)
      }
    )
  }

  def +(other: Vec): Vec = {
    elementWiseOp(other, _ + _)
  }

  def +(scalar: Number): Vec = {
    Vec(v.map(_ + scalar))
  }

  def -(other: Vec): Vec = {
    elementWiseOp(other, _ - _)
  }

  def -(scalar: Number): Vec = {
    Vec(v.map(_ - scalar))
  }

  def *(other: Vec): Vec = {
    elementWiseOp(other, _ * _)
  }

  def *(scalar: Number): Vec = {
    Vec(v.map(_ * scalar))
  }

  def /(other: Vec): Vec = {
    elementWiseOp(other, _ / _)
  }

  def /(scalar: Number): Vec = {
    Vec(v.map(_ / scalar))
  }

  def **(scalar: Number): Vec = {
    Vec(v.map(_ ** scalar))
  }

  def unary_- : Vec = Vec(v.map(-_))

  def norm: Number = v.map(_ ** 2).reduce(_ + _)

  def dot(other: Vec): Vec = {
    require(other.length == length, "Can't perform dot product of vectors of differnet length")
    Vec(
      v.view
        .zip(other.v)
        .map {
          case (e1, e2) => e1 * e2
        }
        .reduce(_ + _)
    )
  }

  /*
  Methods implementing IndexedSeqOptimized
   */

  def apply(idx: Int): Number = v(idx)

  override def repr: IndexedSeq[Number] = v

  def seq: IndexedSeq[Number] = v

  protected[this] def newBuilder: mutable.Builder[Number, IndexedSeq[Number]] = IndexedSeq.newBuilder[Number]

  override def length: Int = v.length
}

object Vec {

  def apply(numbers: Number*): Vec = Vec(numbers.toVector)

  implicit class RichNumber(val n: Number) extends AnyVal {

    def *(vector: Vector[Number]): Vector[Number] = {
      vector.map(_ * n)
    }
  }
}