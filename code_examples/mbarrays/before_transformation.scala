import scala.reflect._
import scala.util._

object MergeSort {
  def mergeSort[T: ClassTag](ary: Array[T], comp: (T, T) => Boolean): Array[T] = {
    def merge(a: Array[T], b: Array[T]): Array[T] = {
      val res = new Array[T](a.length + b.length)
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
      val a = new Array[T](mid)
      val b = new Array[T](len - mid)

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
    val ary = new Array[Int](len)
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