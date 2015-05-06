---
layout: default
title: Authors
short_title: Authors
---

<img src="/images/mbox.png" width="30%" align="right">
<br/>
<br/>
<br/>
The miniboxing project is developed by [**Vlad Ureche**](http://vladureche.ro) as a PhD topic under the supervision of [**Martin Odersky**](http://lampwww.epfl.ch/~odersky/), in the [**Programming Methods Laboratory (LAMP)**](http://lamp.epfl.ch) at [**École polytechnique fédérale de Lausanne (EPFL)**](http://epfl.ch).
<br/>
<br/>
<br/>

## Commiters

The miniboxing plugin <a href="https://github.com/miniboxing/miniboxing-plugin/" target="_blank">is hosted on github</a>, so you can see the <a href="https://github.com/miniboxing/miniboxing-plugin/graphs/contributors">commit stats here</a>.

* [Vlad Ureche](http://vladureche.ro) - main developer for the miniboxing plugin
* [Cristian Talau](https://github.com/ctalau) - developed the initial miniboxing prototype, as a semester project
* [Milos Stojanovic](https://github.com/milosstojanovic) - `TupleX` accessors, type class tweaks, warnings
* [Romain Beguet](https://github.com/Roldak) - `MbArray` tweaks
* [Aymeric Genet](https://github.com/MelodyLucid) - developed [collection-like benchmarks](https://github.com/MelodyLucid/freezing-ironman) for the miniboxing plugin
* [Dmitry Petrashko](https://github.com/DarkDimius) - fixes
* [Ilya Klyuchnikov](https://github.com/ilya-klyuchnikov) - fixes
* [Nicolas Stucki](https://github.com/nicolasstucki) - `MbArray` fixes
* **&lt;your name here&gt;** - yes, we need your help, ask on the <a href="https://groups.google.com/forum/#!forum/scala-miniboxing" target="_blank">mailing list</a>.

## Value Class plugin

The <a href="https://github.com/miniboxing/value-plugin" target="_blank">value class plugin</a> is an prototype that enables multi-parameter <a href="http://docs.scala-lang.org/sips/completed/value-classes.html" target="_blank">value classes for Scala</a>. It relies on the same basic transformation as the miniboxing plugin, [late data layout](/ldl/index.html).

* [Eugene Burmako](https://github.com/xeno-by) - the [value class](https://github.com/miniboxing/valium) plugin based on the [LDL transformation](https://github.com/miniboxing/miniboxing-plugin/blob/wip/docs/2014-03-ldl-draft.pdf?raw=true)
* [Pablo Guerrero](https://github.com/siriux) - the [value class benchmarks](https://github.com/miniboxing/value-benchmarks), which explore the performance of different encodings for multi-parameter value classes

## Acknowledgements

A lot of people helped realize the dream of miniboxing, in many ways. Here's an incomplete list:

* Martin Odersky, for his patient guidance
* Eugene Burmako, for trusting the idea enough to develop the [value-plugin](https://github.com/miniboxing/value-plugin) based on the [LDL transformation](https://github.com/miniboxing/miniboxing-plugin/blob/wip/docs/2014-03-ldl-draft.pdf?raw=true)
* Iulian Dragos, for his work on specialization and many explanations
* Miguel Garcia, for his original insights that spawned the miniboxing idea
* Michel Schinz, for his wonderful comments and enlightening [ACC course](http://lamp.epfl.ch/teaching/advanced_compiler)
* Andrew Myers and Roland Ducournau for the discussions we had and the feedback provided
* Heather Miller for the eye-opening discussions we had
* Vojin Jovanovic, Sandro Stucki, Manohar Jonalagedda and the whole LAMP laboratory in EPFL for the extraordinary athmosphere -- when we're all in the room, sparks fly.
* Adriaan Moors, for the `miniboxing` name which stuck :))
* Thierry Coppey, Vera Salvisberg and George Nithin, who patiently listened to many presentations and provided valuable feedback
* Grzegorz Kossakowski, for the many brainstoring sessions on specialization
* Erik Osheim, Tom Switzer and Rex Kerr for their guidance on the Scala community side
* OOPSLA paper and artifact reviewers, who reshaped the paper with their feedback
* Sandro, Vojin, Nada, Heather, Manohar - reviews and discussions on the [LDL paper](https://github.com/miniboxing/miniboxing-plugin/blob/wip/docs/2014-03-ldl-draft.pdf?raw=true)
* Hubert Plociniczak for the type notation in the [LDL paper](https://github.com/miniboxing/miniboxing-plugin/blob/wip/docs/2014-03-ldl-draft.pdf?raw=true)
* Denys Shabalin, Dmitry Petrashko for their patient reviews of the [LDL paper](https://github.com/miniboxing/miniboxing-plugin/blob/wip/docs/2014-03-ldl-draft.pdf?raw=true)

If your name should be here, [email Vlad](http://www.google.com/recaptcha/mailhide/d?k=016G8jVns__CP-wXgkd4YaJA==&c=lsV9Di2CEGkQFT3zBye4HO80J5SgNrzCv4Dv7tF30KY=)!
