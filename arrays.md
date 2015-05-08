---
layout: default
title: MbArray Tutorial
short_title: MbArray Tutorial
---

`MbArray` is an indexed sequence container that matches the performance of raw `Array`s when used in miniboxed contexts. Also, unlike `Array`s, `MbArray` creation does not require the presence of a `ClassTag`, which makes it more versatile. This page will describe the motivation behind `MbArray` and will show how to use it through examples. 

## Motivation

Raw `Array`s offer the best access performance for primitive types, but they can only be instantiated in generic contexts if a `ClassTag` is present -- which is not always possible. Consider typing the following code in the REPL:

{% highlight scala %}
scala> class A[T](len: Int) {
     |   val array = new Array[T](len)
     | }
<console>:8: error: cannot find class tag for element type T
       val array = new Array[T](len)
{% endhighlight %}

This could be addressed, at the expense of performance, by letting the Scala runtime have access to type information about `T` using a `ClassTag` from `scala.reflect`: 

{% highlight scala %}
scala> import scala.reflect._
import scala.reflect._

scala> class B[T: ClassTag](len: Int) {
     |   val array = new Array[T](len)
     | }
defined class B
{% endhighlight %}

While this wasn't too difficult for class B, the transformation is not possible in the general case because it affects all generics transitively:

{% highlight scala %}
scala> class C[T] extends B[T]
<console>:16: error: No ClassTag available for T
       class C[T] extends B[T]
                          ^
{% endhighlight %}

Thus, in many applications, programmers have resorted to allocating arrays of object and casting them as generic arrays, an approach which is both incorrect and slow:

{% highlight scala %}
scala> def baz[T] = new Array[AnyRef](10).asInstanceOf[Array[T]]
baz: [T]=> Array[T]

scala> baz[Int](0)
java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [I
  ... 33 elided. 
  
{% endhighlight %}

Instead, `MbArray` combined with the miniboxing transformation offers performance that are similar to those of raw `Array`s without requiring to carry around a `ClassTag`. The following code will work without requiring any condition on `T`. On top of that, any read or write to the array will perform better.

{% highlight scala %}
scala> class C[@miniboxed T](len:Int) {
     |   val array = MbArray.empty[T](len)
     | }
Specializing class C...

  ... (The miniboxing transformation operates)

defined class C
{% endhighlight %}

`MbArray` is included in the runtime support package for the miniboxing transformation. To see how to add miniboxing to your project, please see [this page](using_sbt.html).

## Usage

Let's take a closer look at how exactly a program can be transformed to take full advantage of the miniboxing transformation and `MbArray`s. Consider a classic implementation of the merge sort algorithm, at first using a raw `Array` with an implicit `ClassTag`:

{% highlight scala %}

def mergeSort[T: ClassTag](ary: Array[T], comp: (T, T) => Boolean): Array[T] = {
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

To make this page more readable, we removed most of the merge sort algorithm only to keep the lines that are relevant to the tutorial, that is, the lines where the `Array`s are instantiated. If you would like to see the whole source code, [it can be found here](code_examples/mbarrays/before_transformation.scala).

### The transformation

Now let's transform the code above such that it uses miniboxing and `MbArray`s. 
First, we should configure the project so that it includes the Miniboxing plugin. [You can find more information about this step here](using_sbt.html).

When rebuilding the project, we observe that the miniboxing plugin yields different warnings: (here, only keeping those that are related to the `ClassTag` version of the MergeSort)

{% highlight bash %}
[warn] .../Main.scala:11: The following code could benefit from miniboxing specialization
if the type parameter T of method mergeSort would be marked as "@miniboxed T" 
(it would be used to instantiate miniboxed type parameter T1 of trait MiniboxedFunction2)
[warn]         if (comp(a(ai), b(bi))) {
[warn]             ^
[warn] .../Main.scala:53: Use MbArray instead of Array and benefit from miniboxing specialization
[warn]     val ary = new Array[Int](len)
[warn]               ^
[warn] .../Main.scala:58: The method MergeSort.mergeSort would benefit from miniboxing 
type parameter T, since it is instantiated by a primitive type.
[warn]     val sorted = mergeSort(ary, (a: Int, b: Int) => a < b)
[warn]                  ^
[warn] three warnings found
{% endhighlight %}

It is essentially telling us that our code is suboptimal and that it could make it faster if we were to follow the advice that are given for each warning. Let's add the `@miniboxed` annotation on the type parameter `T` of the `MergeSort` method. Recompiling will yield: 

<!-- Adding the warning by hand -->
{% highlight bash %}
[warn] .../Main.scala:7: Use MbArray instead of Array and benefit from miniboxing specialization
[warn]     val res = new Array[T](a.length + b.length)
[warn]               ^
[warn] .../Main.scala:53: Use MbArray instead of Array and benefit from miniboxing specialization
[warn]     val ary = new Array[Int](len)
[warn]               ^
[warn] two warnings found
{% endhighlight %}

Notice that adding `@miniboxed` annotations enabled a new set of optimizations: Since our array instantiations are now done in a miniboxed context, we are suggested to replace `Array`s with `MbArray`s. At this point, it is not necessary anymore to keep the `ClassTag` bound on `T`: We can (and should !) safely remove it .
The final transformation looks like this: 

{% highlight scala %}

def mergeSort[@miniboxed T](ary: MbArray[T], comp: (T, T) => Boolean): MbArray[T] = {
  def merge(a: MbArray[T], b: MbArray[T]): MbArray[T] = {
    val res = MbArray.empty[T](a.length + b.length)
    ...
  }
  val len = ary.length
  if (len <= 1) ary
  else {
    val mid = len / 2
    val a = MbArray.empty[T](mid)
    val b = MbArray.empty[T](len - mid)
    ...
    merge(mergeSort(a, comp), mergeSort(b, comp))
  }
}
  
{% endhighlight %}

You can find the complete version of the transformed code [here](code_examples/mbarrays/after_transformation.scala)

### Benchmarks

We benchmarked the merge sort algorithm implementation above with different sizes of array and ended up with the numbers of the table below (in milliseconds). Here, even though our main goal is to compare `MbArray` against `Array` with `ClassTag`, we also included the `Array[Any]` version, which is one of the current way to bypass the limitations of generic array instantiation as it has been briefly mentionned above, as well as the `Array[Int]` version, which is the ideal, hand-specialized version of the benchmark (not generic).

| Array Size    | `MbArray` | `Array` with `ClassTag` |  `Array[Any]` | `Array[Int]`  |
| ------------- |-----------|-------------------------|---------------|---------------|
| 500'000       | 521       | 856  (<font color="red"> +64% </font>) | 487 (<font color="green"> -7% </font>) | 132 (<font color="green"> -75% </font>) |
| 1'000'000     | 1089      | 1639 (<font color="red"> +50% </font>) | 1134 (<font color="red"> +4% </font>) | 309 (<font color="green"> -72% </font>) |
| 3'000'000     | 3536      | 5349 (<font color="red"> +51% </font>) | 4110 (<font color="red"> +16% </font>) | 855 (<font color="green"> -76% </font>) |

We can observe an average speedup of approximately 40%.
Note that the Array with ClassTag version is compiled without the miniboxing plugin.

You can try it yourself by downloading the benchmarks [here](https://github.com/Roldak/mb-benchmarks).

## Conclusion

* Raw `Array`: Performs well, but cannot be instantiated in generic contexts.
* Raw `Array` with `ClassTag`: Can be instantiated in generic contexts, but introduces performance overhead.
* `MbArray`: Can be instantiated in generic contexts, and will perform well if the generic context is miniboxed.

`MbArray` is therefore a great choice of the underlying container for any custom collection, as it does not impose additional conditions on the type parameter(s) of the collection, without compromising the performance.

{% include status.md %}
