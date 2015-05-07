import scala.reflect._
import scala.util._
import MbArray._

object MergeSort {
  def mergeSort[@miniboxed T](ary: MbArray[T], comp: (T, T) => Boolean): MbArray[T] = {
    def merge(a: MbArray[T], b: MbArray[T]): MbArray[T] = {
      val res = MbArray.empty[T](a.length + b.length)
      var ai = 0
      var bi = 0
      while (ai < a.length && bi < b.length) {
        if (comp(a(ai), b(bi))) {
          res(ai + bi) = a(ai)
          ai += 1
        } else {
          res(ai + bi) = b(bi)
          bi += 1
        }
      }
      while (ai < a.length) {
        res(ai + bi) = a(ai)
        ai += 1
      }
      while (bi < b.length) {
        res(ai + bi) = b(bi)
        bi += 1
      }
      res
    }
    val len = ary.length
    if (len <= 1) ary
    else {
      val mid = len / 2
      val a = MbArray.empty[T](mid)
      val b = MbArray.empty[T](len - mid)

      var i = 0
      while (i < mid) {
        a(i) = ary(i)
        i += 1
      }
      while (i < len) {
        b(i - mid) = ary(i)
        i += 1
      }

      merge(mergeSort(a, comp), mergeSort(b, comp))
    }
  }

  final val len = 50

  def main(args: Array[String]) = {
    val ary = MbArray.empty[Int](len)
    val rnd = new Random
    for (i <- 0 until len) {
      ary(i) = rnd.nextInt(len)
    }
    val sorted = mergeSort(ary, (a: Int, b: Int) => a < b)

    for (i <- 0 until sorted.length) {
      print(sorted(i))
      print(" ")
    }
  }
}