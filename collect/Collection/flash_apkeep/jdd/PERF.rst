JDD performance
***************

JDD is written in Java, and top performance was not the main priority during design.

Still, JDD is a very efficient BDD package. The computation speed is in some situations 
comparable to CUDD and BuDDy. Due to a new cache scheme, JDD sometimes even outperforms 
those two (this depends very much on the size and type of the problem being solved). The 
main disadvantage of using JDD is its memory usage.


If you still need a faster BDD package, check out `JBDD <https://bitbucket.org/vahidi/jbdd>`_, my Java interface to BuDDy and CUDD.


Benchmarks
----------

Benchmarking is hard, specially when performance can vary greatly with minor adjustments.
Still, when this project started many years ago we ran some quick tests to convince people that JDD was fast enough::


  Package  Slow ratio N queens        Slow ratio, CNF SAT
              (12 x Queens)        (aim-100-6_0-yes1-2)
  BuDDy           1.0                        1.6
  CUDD            1.65                       ?
  SableJBDD       > 10                       ?
  JavaBDD         2.5                        5.15
  JDD/BDD         1.55                       1.0
  JDD/ZBDD        0.9                        N/A
  JDD/ZBDD-CSP    0.29                       N/A
  JDD/GSAT       N/A                        < 0.02

Notes:

1. SableJBDD lacks garbage collection
2. at that time JavaBDD was in alpha and has improved since then
3. GSAT is no longer included in JDD

BDD Traces
==========

A BDD-trace is a series of calls to a BDD library recorded at operator level (AND, OR, etc).
With a trace driver, you can repeat these operations with another BDD package.
This allows you to easily compare the performance of two BDD packages.

JDD distribution includes a trace driver (BDDTrace.java) plus the following traces:

* the original traces in Bwolen Yang's trace driver [1].
* ISCAS85 traces (including the C6288 multiplier) by Yirng-An Chen.
* Superscalar Suite 1.0 by Miroslav Velev.

See the *profile.sh* script for more information.

---

  [1] some of these models are explained in the NuSMV manual.
