package epfl.distributed.data

import spire.math._
import spire.random.rng.Cmwc5
import spire.random.{Exponential, Gaussian, Uniform}

import scala.collection.{IndexedSeqOptimized, mutable}

case class Dense(v: IndexedSeq[Number]) extends Vec with IndexedSeqOptimized[Number, IndexedSeq[Number]] {
  require(v.nonEmpty, "A vector cannot be empty")

  def apply(indices: Iterable[Int]): Dense = Dense(indices.map(v(_)).toIndexedSeq)

  def elementWiseOp(other: Vec, op: (Number, Number) => Number): Vec = {
    require(other.size == size, "Can't perform element-wise operation on vectors of different length")

    other match {
      case Dense(otherValues) =>
        Dense(
          v zip otherValues map {
            case (e1, e2) => op(e1, e2)
          }
        )

      case s: Sparse => s.elementWiseOp(this, op)
    }
  }

  override def mapValues(op: Number => Number): Vec = Dense(v.map(op))

  override def sparse: Sparse = {
    Sparse(
      v.indices
        .zip(v)
        .filter {
          case (_, num) => abs(num) > Sparse.epsilon
        }
        .toMap,
      length
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

object Dense {

  def apply(numbers: Number*): Dense = Dense(numbers.toVector)

  def zeros(size: Int): Dense                             = Dense(Vector.fill(size)(Number.zero))
  def ones(size: Int): Dense                              = Dense(Vector.fill(size)(Number.one))
  def fill(value: Number, size: Int): Dense               = Dense(Vector.fill(size)(value))
  def oneHot(value: Number, index: Int, size: Int): Dense = Dense(Vector.fill(size)(Number.zero).updated(index, value))

  implicit private[this] val rng: Cmwc5 = Cmwc5()

  def randU[N <: Number: Uniform](size: Int, min: N, max: N) = Dense(Uniform(min, max).sample[Vector](size))
  def randG[N <: Number: Gaussian](size: Int, mean: N = 0d, stdDev: N = 1d) =
    Dense(Gaussian(mean, stdDev).sample[Vector](size))
  def randE[N <: Number: Exponential](size: Int, rate: N) = Dense(Exponential(rate).sample[Vector](size))

  implicit class RichNumber(val n: Number) extends AnyVal {

    def *(vector: Vector[Number]): Vector[Number] = {
      vector.map(_ * n)
    }
  }

}