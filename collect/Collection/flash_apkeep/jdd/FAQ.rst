JDD Frequently Asked Questions
******************************

This is a list of the JDD library Frequently Asked Questions (FAQ). It contains answers to some frequently asked questions about the JDD library.

Introduction (what is this all about?)
--------------------------------------

**What is this?**

This is a package for working with Binary Decision Diagrams (BDDs) and Zero-suppressed BDDs.

**What is it good for?**

BDDs are used to efficiently represent and manipulate functions that map a binary vector to a boolean value (i.e. f: 2^V -> {0,1}). This representation can be used to efficiently store huge sets/functions/relations in relatively small chunks of memory. It can be used in to solve large combinatorial and graph problems. There are many application from CAD to compiler optimization to Internet firewall technology.


Copyrights
----------

**Sounds good, can I use it in my project?**

Yes

**Can I use JDD in my commercial application?**

Yes. But make sure you read the zlib license first.

**Are there any patented algorithms used in JDD?**

To best of my knowledge, no


Performance
-----------

**How fast is JDD Compared to other packages?**

It is written in Java. Often it is fast enough.

**Why is JDD so slow on some benchmark problems??**

JDD lacks dynamic variable re-ordering. Therefore, some benchmark problems that use a very bad initial variable ordering cannot complete in reasonable time.

**What is dynamic variable re-ordering anyway??**

The size of a BDD representing a boolean functions may differ drastically depending on the order in which the boolean variables are introduced in the BDD. There exists several algorithms to re-arrange the variable order on the fly to make the BDDs smaller.

**Why doesn't JDD support dynamic variable re-ordering??**

*For some (most?) problems*, a good initial variable ordering is much more efficient than using dynamic variable re-ordering. In fact, as Bwolen Yang's PhD thesis showed, re-ordering is often too costly. This is specially true for sequential problems, which JDD was initially written for.

Beside, not including variable re-ordering made the JDD code less complex...

**Why does JDD fail to allocate more memory even when large chunks are available?**

This has to do with the heap-compacting algorithm of the Java virtual machine.

In earlier versions JDD would try to remedy this by using fragmented compressed memory.
This did not work very well in practice and we removed it.

**Why is my BDD application so slow? I thought BDDs were very efficient?**

There may be several reasons: bad variable ordering is the most probable. Maybe you are solving your problem in a wrong way? Maybe what you really need is a SAT-solver? Maybe the problem is intractable anyway? Check out Alan Hu's PhD thesis (Stanford) and Bwolen Po-Jen Yang's PhD thesis (CMU) for some basic guidelines on the subject.


**Why is JDD so fast on the C6288 multiplier benchmark?**

This is an example of a type of model where you cannot "cheat" by pre-computing a very good initial variable order and use that instead of the given variable ordering :)

Technical details
-----------------

**How many bytes does each BDD node occupy in JDD?**

On 32-bit machines: 16 bytes in the node-table, in addition, about 8 bytes are required for each node in the hash-table. So the actual size is 24.

**What kind of garbage collection is used in JDD?**

Top-level reference counting + mark and sweep.

**Does JDD use depth or breadth-first node traversal ??**

Depth first

**What is the maximal number of nodes than can exists in JDD?**

Somewhere between 2^26 and 2^31 nodes, but this is all just theoretical.

It should be noted that the largest practical problem we have seen so far required about 10 million nodes.
A general rule of thumb is that if you are above 3 million nodes, then you should either re-think your algorithm or use another technique than BDDs...

**What is the maximal number of variables that can be used in JDD?**

Again, 2^31 in theory and about 40 million in practice, which should still be enough for most people ;)

Development
-----------

**How can I contribute to the JDD project?**

There are several ways:

* Implement some fancy BDD algorithms
* Submit bug reports!
* Write a benchmark
* Find a nice way to solve some well known problem with BDDs
* ...

**What version of JDD am I running?**

We use an increasing build number to indicate changing versions. see the jdd.Version.build field.


Features
--------

**The BDD library is extremely low-level, I need a more user-friendly API!**

The core BDD library is low-level to give you complete control over each computation. We understand that programming at such a low level is error prone, you will simply have to write your own high-level wrappers.

NEW: The friendly people of the JavaBDD project have created a wrapper for JDD which allows you to use their high-level interface with JDD. Give it a try!

**Please implement complemented edges**

This increases complexity and seeing CUDD performance it might not be worth it.

**I would like to see the algorithm "xyz" in the next version of JDD**

Sure, but if it is too much work, we might send you the source code and ask you to implement it yourself :)


Common Problems (stuff that usually fill my mailbox)
----------------------------------------------------

**How can I submit a bug?**

Use the `issue tracker <https://bitbucket.org/vahidi/jdd/issues>`_...

**I have an example of a problem on which JDD performs very poor, what should I do?**

Send us the example and we will look at it.


**I can't get the DOT output working!**

Dot is a third-party utility provided by AT&T research. Make sure you first download it and install it from http://www.research.att.com/sw/tools/graphviz/. If you still get errors like this:
::

  java.io.IOException: CreateProcess: ...

Then probably dot is not in your path.

**I want the source DOT file, but all I get is its picture...**

By default, when you create a DOT file you will get a PNG-image. to get the source file instead, try this:
::

  import jdd.util.*;
  [...]
  Dot.setRemoveDotFile(false);
  Dot.setExecuteDot(false);
  bdd.printDot("filename.dot", somebdd);

**Whats wrong with this code?**
::

  BDD bdd = new BDD();
  [...]
  int bdd1 = bdd.and(somevariable, anothervaribale);
  int bdd2 = bdd.and(thirdvariable, andsoon);
  int bdd3 = bdd.or( bdd1, bdd2);

This code is dead wrong! Since you are not adding a ref-count to "bdd1", it may get garbage collected (for example, during the second "and") and then when you try the "or", "bdd1" is not a valid bdd anymore and the result of this operation is garbage. Do this instead:
::

  int bdd1 = bdd.ref( bdd.and(somevariable, anothervaribale) );
  int bdd2 = bdd.ref( bdd.and(thirdvariable, andsoon) );
  int bdd3 = bdd.ref( bdd.or( bdd1, bdd2) ); // yes, this one too. you will need it later on, wont you?

  bdd.deref(bdd2);
  bdd.deref(bdd1);

One way to catch such problems is to use a "DebugBDD" manager instead of "BDD". Beware however that it is very slow and might not catch all problems.

**Whats wrong with this other code?**
::

  BDD bdd = new BDD();
  [...]
  int bdd1 = bdd.ref( bdd.and(somevariable, anothervaribale) );
  int bdd2 = bdd.and(thirdvariable, andsoon);
  int bdd3 = bdd.ref( bdd.or( bdd1, bdd2) );
  bdd.deref(bdd3);

Nothing really. "bdd2" is not ref-counted, but nothing happens between the creation of "bdd2" and the call to "or" so "bdd2" cannot not be garbage collected. During the "or" itself, "bdd2" is protected from garbage collection by JDD.

Note however that "DebugBDD" will catch this as a possible error!.

Misc.
-----

**Can several BDD managers simultaneously exist in JDD?**

Yes. 

Note however that you cannot move things freely between different managers, you will need to implement some routine that transforms variables and trees first.

**JDD calls System.exit() on fatal errors. How can I change that?**

Override NodeTable.fatal() and throw an Error instead of calling System.exit().

**Are Z-BDDs better than BDDs?**

Z-BDDs represent sparse sets more efficiently. They are probably more useful for representing things such as Petri nets and graphs.

**What is the relation between JDD and BuDDy?**

JDD uses the same internal structure as BuDDy. In fact, few operations are copy-pasted from the BuDDy source :)

**What is the relation between JDD and CUDD?**

JDD and CUDD use the same hash functions, that is the only relation I can think of...
