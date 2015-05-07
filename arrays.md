---
layout: default
title: MbArray Tutorial
short_title: MbArray Tutorial
---

`MbArray` is an indexed sequence container that matches the performance of raw arrays when used in miniboxed contexts. Also, unlike arrays, MbArray creation does not require the presence of a ClassTag, which makes it more versatile. This page will describe the motivation behind MbArray and will show how to use it through examples. 

## Motivation

Raw arrays offer the best access performance for primitive types, but they can only be instantiated in generic contexts if a ClassTag is present -- which is not always possible. Consider typing the following code in the REPL :

{% highlight scala %}
scala> class A[T](len: Int) {
     |   val array = new Array[T](len)
     | }
{% endhighlight %}

Pressing enter will result in the following error :

{% highlight scala %}
<console>:8: error: cannot find class tag for element type T
       val array = new Array[T](len)
{% endhighlight %}

This could be addressed, at the expense of performance, by letting the scala runtime have access to type informations about `T` using a `ClassTag` from `scala.reflect` : 

{% highlight scala %}
scala> import scala.reflect._
import scala.reflect._

scala> class B[T : ClassTag](len: Int) {
     |   val array = new Array[T](len)
     | }
defined class B
{% endhighlight %}

While this works, it may not be sufficiently fast for algorithms where high performance is expected. Instead, `MbArray` combined with the miniboxing transformation offers performances that are similar to those of raw arrays without having to carry around a `ClassTag`. The following code will work without requiring any condition on `T`. On top of that, any read or write to the array will perform better.

{% highlight scala %}
scala> class C[@miniboxed T](len :Int) {
     |   val array = MbArray.empty[T](len)
     | }
Specializing class C...

  ... (The miniboxing transformation operates)

defined class C
{% endhighlight %}

MbArrays are included in the runtime support package for the miniboxing transformation. To see how to add miniboxing to your project, please see [this page](using_sbt.html).

## Usage

Let's take a closer look at how exactly a program can be transformed to take full advantage of the miniboxing transformation and MbArrays. Consider a classic implementation of the merge sort algorithm, at first using a raw `Array` with an implicit `ClassTag` :

{% highlight scala %}

def mergeSort[T : ClassTag](ary: Array[T], comp: (T, T) => Boolean): Array[T] = {
  def merge(a: Array[T], b: Array[T]): Array[T] = {
    val res = new Array[T](a.length + b.length)
    ...
  }
  val len = ary.length
  if (len <= 1) ary
  else {
    val mid = len / 2
    val a = new Array[T](mid)
    val b = new Array[T](len - mid)
    ...
    merge(mergeSort(a, comp), mergeSort(b, comp))
  }
}
  
{% endhighlight %}

To make this page more readable, we removed most of the merge sort algorithm only to keep the lines that are relevant to the tutorial, that is, the lines where the `Array`s are instantiated. If you would like to see the whole source code, [it can be found here](https://github.com/Roldak/mb-benchmarks/blob/master/mergesort-no-mb/src/main/scala/Main.scala)).

### The transformation

Now let's transform the code above such that it uses miniboxing and MbArrays. 
First, we should configure the project so that it includes the Miniboxing plugin. [You can find more informations about this step here](using_sbt.html).

When rebuilding the project, we observe that the miniboxing plugin yields different warnings : (here, only keeping those that are related to the `ClassTag` version of the MergeSort)

{% highlight bash %}
[warn] .../mb-benchmarks/mergesort-mb/src/main/scala/Main.scala:107: The following code 
could benefit from miniboxing specialization if the type parameter T of method mergeSort 
would be marked as "@miniboxed T" (it would be used to instantiate miniboxed type parameter 
T1 of traitMiniboxedFunction2)
[warn]         if (comp(a(ai), b(bi))) {
[warn]             ^
[warn] .../mb-benchmarks\mergesort-mb\src\main\scala\Main.scala:226: The method 
miniboxing.example.MergeSort.mergeSort would benefit from miniboxing type parameter T, 
since it is instantiated by a primitive type.
[warn]       mergeSortCT(aryC, (a: Int, b: Int) => a < b)
[warn]       ^
[warn] ... warnings found
{% endhighlight %}

It is essentially telling us that our code is suboptimal and that it could make it faster if we were to follow the advices that are given for each warning. Let's add the `@miniboxed` annotation on the type parameter `T` of the `MergeSort` method. Recompiling will yield : 

// TODO do the rest

1. Let's first add the line `import MbArray._`.
2. Then, replace all occurences of the type `Array[T]` by `MbArray[T]`, and all the array instantiations `new Array[T](...)` by `MbArray.empty[T](...)`. 
3. Finally, remove the `ClassTag` bound on the type parameter `T`.

Compiling at this point will yield the following output :

{% highlight bash %}
[warn] (...) The method MergeSort.mergeSort would benefit from miniboxing type parameter T, 
since it is instantiated by a primitive type.
[warn]     val sorted = mergeSort(ary, (a: Int, b: Int) => a < b)
[warn]                  ^
[warn] (...) The following code instantiating an `MbArray` object cannot be optimized since the 
type argument is not a primitive type (like Int), a miniboxed type parameter or a subtype of 
AnyRef. This means that primitive types could end up boxed:
[warn]    val res = MbArray.empty[T](a.length + b.length)
[warn]                      ^
[warn] (...) The following code instantiating an `MbArray` object cannot be optimized since the 
type argument is not a primitive type (like Int), a miniboxed type parameter or a subtype of 
AnyRef. This means that primitive types could end up boxed:
[warn]    val a = MbArray.empty[T](mid)
[warn]                    ^
[warn] (...) The following code instantiating an `MbArray` object cannot be optimized since the 
type argument is not a primitive type (like Int), a miniboxed type parameter or a subtype of 
AnyRef. This means that primitive types could end up boxed:
[warn]    val b = MbArray.empty[T](len - mid)
[warn]                    ^
[warn] 5 warnings found
{% endhighlight %}
 
The miniboxing plugin informs us that code is suboptimal and could get faster if we were to use the `@miniboxed` annotation on the type parameter `T`. After proceeding and compiling again, we observe that there are no more warnings and our code has been successfully optimized by the miniboxing transformation. The final transformation looks like this : 
{% highlight scala %}

import scala.reflect._
import scala.util._

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
  }
}
  
{% endhighlight %}
### Benchmarks

We benchmarked the merge sort algorithm implementation above with different sizes of array and ended up with the following numbers (in milliseconds) :

| Array Size    | Array with ClassTag  | MbArray  | Improvement |
| ------------- |----------------------| ---------|-------------|
| 500'000       | 713    	       | 528      | 35%   	|
| 1'000'000     | 1536                 | 1163     | 32%		|
| 3'000'000     | 5311                 | 3545     | 50%    	|

We can observe an average speedup of approximately 40%.
Note that the Array with ClassTag version is compiled without the miniboxing plugin.
You can try it yourself by downloading the benchmarks [here](https://github.com/Roldak/mb-benchmarks).

## Conclusion

* Raw Arrays : Perform well, but cannot be instantiated in generic contexts.
* Raw Arrays with `ClassTag` : Can be instantiated in generic contexts, but introduces performance overhead.
* MbArrays with the Miniboxing transformation : Can be instantiated in generic contexts, and will perform well if the generic context is miniboxed.

MbArray is therefore a great choice of the underlying container for any custom collection, as it does not impose additional conditions on the type parameter(s) of the collection, without compromising the performances.

{% include status.md %}
