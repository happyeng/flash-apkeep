package jdd.zdd;


import jdd.util.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestZDD {

	@Test public void testBasic() {
		ZDD zdd = new ZDD(100);

		// test some basic stuffs first:
		assertEquals("false.top", -1, zdd.getVar(0));
		assertEquals("true.top", -1, zdd.getVar(1));

		int x1 = zdd.createVar();
		int x2 = zdd.createVar();

		int a = zdd.empty();
		int b = zdd.base();
		int c = zdd.change(b, x1);
		int d = zdd.change(b, x2);
		int e = zdd.union(c,d);
		int f = zdd.union(b,e);
		int g = zdd.diff(f,c);


		// directly from minatos paper, figure 9
		// [until we find a better way to test isomorphism...]
		assertEquals("emptyset = 0", 0, a);
		assertEquals("base = 1", 1, b);
		assertEquals("C", c, zdd.mk(x1,0,1));
		assertEquals("D", d, zdd.mk(x2,0,1));
		assertEquals("e", c, zdd.getLow(e));
		assertEquals("E", 1, zdd.getHigh(e));

		int tmp = zdd.mk(x1, 1,1);
		assertEquals("F", tmp, zdd.getLow(f));
		assertEquals("F", 1, zdd.getHigh(f));
		assertEquals("G", g, zdd.mk(x2, 1,1));


		// intersect
		assertEquals("intersect (1)", zdd.intersect(a, b), a);
		assertEquals("intersect (2)", zdd.intersect(a, a), a);
		assertEquals("intersect (3)", zdd.intersect(b, b), b);
		assertEquals("intersect (4)", zdd.intersect(c, e), c);
		assertEquals("intersect (5)", zdd.intersect(e, f), e);
		assertEquals("intersect (6)", zdd.intersect(e, g), d);



		// union
		assertEquals("union (1)", zdd.union(a, a), a);
		assertEquals("union (2)", zdd.union(b, b), b);
		assertEquals("union (3)", zdd.union(a, b), b);
		assertEquals("union (4)", zdd.union(g, e), f);

		// diff:
		assertEquals("diff (1)", zdd.diff(a,a), a);
		assertEquals("diff (2)", zdd.diff(b,b), a);
		assertEquals("diff (3)", zdd.diff(d,c), d);
		assertEquals("diff (4)", zdd.diff(c,d), c);
		assertEquals("diff (5)", zdd.diff(e,c), d);
		assertEquals("diff (6)", zdd.diff(e,d), c);
		assertEquals("diff (7)", zdd.diff(g,b), d);

		assertEquals("diff (8)", zdd.diff(g,d), b);
		assertEquals("diff (9)", zdd.diff(f,g), c);
		assertEquals("diff (10)", zdd.diff(f,e), b);


		// subset0
		assertEquals("subset0 (1)", zdd.subset0(b, x1), b);
		assertEquals("subset0 (2)", zdd.subset0(b, x2), b);
		assertEquals("subset0 (3)", zdd.subset0(d, x2), a);
		assertEquals("subset0 (4)", zdd.subset0(e, x2), c);

		// subset1
		assertEquals("subset1 (1)", zdd.subset1(b, x1), 0);
		assertEquals("subset1 (2)", zdd.subset1(b, x2), 0);
		assertEquals("subset1 (3)", zdd.subset1(d, x2), b);
		assertEquals("subset1 (4)", zdd.subset1(g, x2), b);
		assertEquals("subset1 (5)", zdd.subset1(g, x1), a);
		assertEquals("subset1 (6)", zdd.subset1(e, x2), b);
	}

	@Test public void testBasic2() {
		// this is the exact construction sequence of Fig.14 in "Zero-suppressed BDDs and their application" by Minato

		ZDD zdd = new ZDD(100);

		int x1 = zdd.createVar();
		int x2 = zdd.createVar();
		int x3 = zdd.createVar();
		int x4 = zdd.createVar(); // not used
		int tmp = zdd.union(1, zdd.change(1, x1));
		int tmp2 = zdd.change(tmp, x2);
		tmp = zdd.union(tmp, tmp2);
		int fig14 = zdd.union(tmp, zdd.change(1, x3));


		// this is the exact construction sequence of Fig.13 in "Zero-suppressed BDDs and their application" by Minato
		boolean [] minterm = new boolean[4];
		minterm[3] = minterm[2] = minterm[1] = true; minterm[0] = false;
		tmp = zdd.subsets(minterm);
		minterm[0] = true; minterm[1] = false;
		tmp2 = zdd.subsets(minterm);
		tmp = zdd.intersect(tmp,tmp2);

		minterm[3] = minterm[0] = minterm[1] = true; minterm[2] = false;
		tmp2 = zdd.subsets(minterm);
		tmp = zdd.union(tmp2, tmp);
		minterm[2] = minterm[3] = minterm[0] = true; minterm[3] = false;
		tmp2 = zdd.subsets(minterm);
		int fig13 = zdd.intersect(tmp2, tmp);

		assertEquals("Fig.13 and Fig.14 yield equal result", fig13, fig14);


		// some other tests from minatos paper "ZBDDs and their applications..."
		// 1. INTERSECT
		tmp  = zdd.cubes_union("100 011 010");
        tmp2 = zdd.union( zdd.cube("11"), 1);
        int tmp3 = zdd.intersect(tmp, tmp2);
		int answer = zdd.cube("11");
		assertEquals("intersect test", tmp3, answer);
		assertEquals("TOS restored after intersect", zdd.debug_nstack_size(), 0);

		// 2. UNION
		tmp3 = zdd.union(tmp, tmp2);
		answer = zdd.union(tmp, 1);
		assertEquals("union test", tmp3, answer);
		assertEquals("TOS restored after union", zdd.debug_nstack_size(), 0);

		// 3. DIFF
		tmp3 = zdd.diff(tmp, tmp2);
		answer = zdd.union( zdd.cube("10"), zdd.cube("100") );
		assertEquals("diff test", tmp3, answer);
		assertEquals("TOS restored after diff", zdd.debug_nstack_size(), 0);
	}
}
