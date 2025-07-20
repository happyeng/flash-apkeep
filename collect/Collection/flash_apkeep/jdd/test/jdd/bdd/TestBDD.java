package jdd.bdd;

import jdd.util.*;
import jdd.util.math.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestBDD {


	@Test public void testCreateVar() {
		BDD jdd = new BDD(100);
		assertEquals("Initial variable cound correct ", 0, jdd.numberOfVariables());

		int v0 = jdd.createVar();
		int v1 = jdd.createVar();
		assertEquals("Variable cound correct (1) ", 2, jdd.numberOfVariables());

		int [] v23 = jdd.createVars(2);
		assertEquals("Variable cound correct (2) ", 4, jdd.numberOfVariables());

		// check these are all variables:
		int []v03 = new int[] { v0, v1, v23[0], v23[1]};
		for(int i = 0; i < v03.length; i++) {
			final int v = v03[i];
			assertTrue("variable type", jdd.isVariable(v));
			assertEquals("Variable id", jdd.getVar(v), i);
			assertEquals("Variable low", jdd.getLow(v), 0);
			assertEquals("Variable high", jdd.getHigh(v), 1);
		}

	}
	@Test public void testGC() {
		BDD jdd = new BDD(2); // <-- want garbage collections

		int v1 = jdd.createVar();
		int v2 = jdd.createVar();
		int v3 = jdd.createVar();
		int v4 = jdd.createVar();

		// check deadnodes counter
		int dum = jdd.and(v3,v2);
		assertEquals(" no dead nodes at start", 0, jdd.dead_nodes );

		jdd.ref( dum );
		assertEquals(" still no dead nodes", 0, jdd.dead_nodes);

		jdd.deref( dum );
		assertEquals(" one dead node", 1, jdd.dead_nodes);

		jdd.deref( dum );
		assertEquals(" still one dead node", 1, jdd.dead_nodes);

		// test garbage collection:
		jdd.grow(); // make sure there is room for it
		int g1 = jdd.and(v3,v2);
		int g2 = jdd.ref(jdd.or(g1, v1) );
		assertEquals("should not free g1 (recusrive dep)", 0, jdd.gc());
		jdd.deref(g2);

		// jdd.show_table();
		assertEquals("should free g2 thus also g1 (recusrive dep)", 2, jdd.gc());
		jdd.gc(); // Should free g1 and g2
	}


	@Test public void testOperations() {
		BDD jdd = new BDD(2);

		int v1 = jdd.createVar();
		int v2 = jdd.createVar();
		int v3 = jdd.createVar();
		int v4 = jdd.createVar();


		int nv1 = jdd.ref( jdd.not(v1) );
		int nv2 = jdd.ref( jdd.not(v2) );
		int nv3 = jdd.ref( jdd.not(v3) );

		// test restrict:
		int res0 = jdd.ref( jdd.and( v1, nv3) );
		int res1 = jdd.ref( jdd.xor(v1, v2) );
		int res2 = jdd.ref( jdd.not(res1) );
		assertEquals("restrict 1", 1, jdd.restrict( v1, res0));
		assertEquals("restrict 2", v2, jdd.restrict( v2, res0));
		assertEquals("restrict 3", 0, jdd.restrict( v3, res0));
		assertEquals("restrict 4", nv2, jdd.restrict( res1, res0));
		assertEquals( "restrict 5", v2, jdd.restrict( res2, res0));
		jdd.deref(res2);
		jdd.deref(res1);
		jdd.deref(res0);


		// and, or, not [MUST REF INTERMEDIATE STUFF OR THEY WILL DISAPPEAR DURING GC]
		int n1 = jdd.ref(jdd.and(v1,v2));
		int orn12 = jdd.ref( jdd.or(nv1, nv2));
		int n2 = jdd.ref( jdd.not(orn12) );
		assertTrue("BDD canonicity (and/or/not)", n1 == n2);

		// XOR:
		int h1 = jdd.ref( jdd.and( v1, nv2 ) );
		int h2 = jdd.ref( jdd.and( v2, nv1 ) );
		int x1 = jdd.ref( jdd.or( h1, h2) );
		jdd.deref(h1);
		jdd.deref(h2);
		int x2 = jdd.ref( jdd.xor(v1, v2) );
		assertEquals("BDD canonicity (XOR)", x1, x2);
		jdd.deref(x1);
		jdd.deref(x2);



		// biimp
		int b1 = jdd.ref( jdd.or( n1, jdd.and( jdd.not(v1), jdd.not(v2)) ) );
		int b2 = jdd.biimp(v1, v2);
		assertTrue("BDD canonicity (biimp)", b1 == b2);


		// NAND
		int a1 = jdd.ref( jdd.and(v1,v2) );
		int na1 = jdd.ref( jdd.not(a1) );
		jdd.deref(a1);
		int na2 = jdd.ref( jdd.nand( v1, v2) );
		int naeq = jdd.ref( jdd.biimp(na1, na2) );
		assertTrue("NAND consistency", na1 ==  na2);
		jdd.deref(na1);
		jdd.deref(na2);
		jdd.deref(naeq);


		// NOR
		int o1 = jdd.ref( jdd.or(v1,v2) );
		int no1 = jdd.ref( jdd.not(o1) );
		jdd.deref(o1);
		int no2= jdd.ref( jdd.nor( v1, v2) );
		int noeq = jdd.ref( jdd.biimp(no1, no2) );
		assertTrue("NOR consistency", no2 ==  no1);
		jdd.deref(no1);
		jdd.deref(no2);
		jdd.deref(noeq);

		assertEquals("workset stack should be empty", 0, jdd.nstack.getTOS());

		// nodeCount
		assertEquals("nodeCount (1)", 0, jdd.nodeCount( 0));
		assertEquals("nodeCount (2)", 0, jdd.nodeCount( 1));
		assertEquals("nodeCount (3)", 1, jdd.nodeCount( v1));
		assertEquals("nodeCount (4)", 1, jdd.nodeCount( nv2));
		assertEquals("nodeCount (5)", 2, jdd.nodeCount( jdd.and( v1, v2)));
		assertEquals("nodeCount (6)", 3, jdd.nodeCount( jdd.xor( v1, v2)));


		// quasiReducedNodeCount
		assertEquals("quasiReducedNodeCount (1)", 0, jdd.quasiReducedNodeCount( 0));
		assertEquals("quasiReducedNodeCount (2)", 0, jdd.quasiReducedNodeCount( 1));
		assertEquals("quasiReducedNodeCount (3)", 1, jdd.quasiReducedNodeCount( v1));
		assertEquals("quasiReducedNodeCount (4)", 1, jdd.quasiReducedNodeCount( nv2));
		assertEquals("quasiReducedNodeCount (5)", 2, jdd.quasiReducedNodeCount( jdd.and( v1, v2)));
		assertEquals("quasiReducedNodeCount (6)", 3, jdd.quasiReducedNodeCount( jdd.xor( v1, v2)));


		// this shows the difference
		int qs1 = jdd.ref( jdd.xor(v1, v2) );
		int qs2 = jdd.ref( jdd.xor(v3, v4) );
		int qs3 = jdd.ref( jdd.xor(qs1, qs2) );
		assertEquals("quasiReducedNodeCount (7)", 3, jdd.quasiReducedNodeCount( qs1));
		assertEquals("quasiReducedNodeCount (7)", 3, jdd.quasiReducedNodeCount( qs2));
		assertEquals("quasiReducedNodeCount (7)", 15, jdd.quasiReducedNodeCount( qs3));
		assertEquals("nodeCount (7)", 7, jdd.nodeCount( qs3)); // just to be sure
		jdd.deref(qs1);
		jdd.deref(qs2);
		jdd.deref(qs3);


		// satcount
		final float epsilon = 0.001f;
		assertEquals("satCount(0)", 0, jdd.satCount(0), epsilon);
		assertEquals("satCount(1)", 16, jdd.satCount(1), epsilon);
		assertEquals("satCount(v1)", 8, jdd.satCount(v1), epsilon);
		assertEquals("satCount(n1)", 4, jdd.satCount(n1), epsilon);
		assertEquals("satCount(b1)", 8, jdd.satCount(b1), epsilon);



		// Test quantification:
		int cube = jdd.ref( jdd.and(v2, v3));
		int toq  = jdd.ref( jdd.and(v1,v3));
		int qor = jdd.ref( jdd.or(v1,v2) );
		assertTrue("Exist failed (1)", jdd.exists( toq, cube) == v1);
		assertTrue("Exist failed (2)", jdd.exists( nv1, cube) == nv1);
		assertTrue("Forall failed (1)", jdd.forall( toq, cube) == 0);
		assertTrue("Forall failed (2)", jdd.forall( qor, v2) == v1);
		assertTrue("Forall failed (3)", jdd.forall( qor, v1) == v2);
		assertTrue("Forall failed (4)", jdd.forall( qor, v3) == qor);

		// test relProd:
		int rel0 = jdd.ref( jdd.xor(v1, v2) );
		int rel1 = jdd.ref( jdd.relProd( rel0, v1, v1) );
		int rel2 = jdd.ref( jdd.relProd( rel0, nv1, v1) );

		int reltmp = jdd.ref( jdd.and( rel0, v1) );
		int rel1c = jdd.ref( jdd.exists(reltmp, v1) );
		jdd.deref(reltmp);

		reltmp = jdd.ref( jdd.and( rel0, nv1) );
		int rel2c = jdd.ref( jdd.exists(reltmp, v1) );
		jdd.deref(reltmp);

		assertEquals("relProd (1)", rel1, rel1c);
		assertEquals("relProd (2)", rel2, rel2c);
		jdd.deref(rel1c);
		jdd.deref(rel2c);
		jdd.deref(rel0);
		jdd.deref(rel1);
		jdd.deref(rel2);


		// test permutation:
		int []c1 = new int[]{ v1, v2 };
		int []c2 = new int[]{ v3, v4 };
		int []c3 = new int[]{ v1, v3 };
		int []c4 = new int[]{ v2, v4 };

		Permutation perm1 = jdd.createPermutation(c1, c2);
		Permutation perm2 = jdd.createPermutation(c2, c1);
		Permutation perm3 = jdd.createPermutation(c3, c4);
		Permutation perm4 = jdd.createPermutation(c4, c3);


		int v1v2 = jdd.ref( jdd.and(v1,v2) );
		int v3v4 = jdd.ref( jdd.and(v4,v3) );
		int v1v3 = jdd.ref( jdd.and(v1,v3) );
		int v2v4 = jdd.ref( jdd.and(v2,v4) );
		int p1 = jdd.replace( v1v2, perm1);
		int p2 = jdd.replace( v3v4, perm2);
		int p3 = jdd.replace( v1v3, perm3);
		int p4 = jdd.replace( v2v4, perm4);

		assertTrue("replace() test (1)", p1 == v3v4);
		assertTrue("replace() test (2)", p2 == v1v2);
		assertTrue("replace() test (3)", p3 == v2v4);
		assertTrue("replace() test (4)", p4 == v1v3);


		// test support:

		int sx1 = jdd.xor(v2,v3);
		int sx2 = jdd.imp(v1,v3);
		int sx3 = jdd.biimp(v2,v4);
		int s12 = jdd.ref(jdd.cube("1100"));
		int s13 = jdd.ref(jdd.cube("1010"));
		int s24 = jdd.ref(jdd.cube("0101"));
		int s23 = jdd.ref(jdd.cube("0110"));
		int s34 = jdd.ref(jdd.cube("0011"));


		assertEquals("Support (1)", s12, jdd.support(v1v2));
		assertEquals("Support (2)", s13, jdd.support(v1v3));


		assertEquals("Support (3)", s24, jdd.support(v2v4));
		assertEquals("Support (4)", s34, jdd.support(v3v4));
		assertEquals("Support (5)", s23, jdd.support(sx1));
		assertEquals("Support (6)", s13, jdd.support(sx2));
		assertEquals("Support (7)", s24, jdd.support(sx3));


		// now clean up that mess
		jdd.deref(s12);		jdd.deref(s13);		jdd.deref(s24);		jdd.deref(s34);	jdd.deref(s23);
		jdd.deref(p1);		jdd.deref(p2);		jdd.deref(p3);		jdd.deref(p4);
		jdd.deref(v1v2);	jdd.deref(v1v3);	jdd.deref(v2v4);	jdd.deref(v3v4);
}

	@Test public void testTemp() {
		// check temporary nodes in garbage collection:
		BDD jdd = new BDD(7);
		int v1 = jdd.createVar();
		int v2 = jdd.createVar();
		int tmp = jdd.nstack.push(jdd.and(v1,v2)); // use one node...
		jdd.gc();
		assertTrue("intermediate node not garbage collected", jdd.isValid( tmp));
		jdd.nstack.pop();
		jdd.gc();
		assertTrue("previously intermediate node garbage collected", !jdd.isValid( tmp));
	}


	@Test public void testITE() {
		// TEST ITE: taken from the brace/rudell/bryant paper
		BDD jdd = new BDD(100000);
		int v1 = jdd.createVar();
		int v2 = jdd.createVar();

		assertEquals("ITE 1", jdd.and(v1,v2), jdd.ite(v1,v2,0));
		assertEquals("ITE 2", jdd.or(v1,v2), jdd.ite(v1,1,v2) );
		assertEquals("ITE 3", jdd.xor(v1,v2), jdd.ite(v1,jdd.not(v2), v2));
		assertEquals("ITE 4", jdd.not(v1), jdd.ite(v1,0, 1 ) );
		assertEquals("ITE 5", jdd.nor(v1,v2), jdd.ite(v1, 0, jdd.not(v2)));
		assertEquals("ITE 6", jdd.biimp(v1,v2), jdd.ite(v1,v2, jdd.not(v2)));
		assertEquals("ITE 7", jdd.nand(v1,v2), jdd.ite(v1,jdd.not(v2), 1 ));
	}

	@Test public void testOneSat() {
        BDD jdd = new BDD(200);
        int v1 = jdd.createVar();
        int v2 = jdd.createVar();
        int v3 = jdd.createVar();

        int dum = jdd.ref( jdd.not(v2));

        int p1 = jdd.ref( jdd.and(v1, dum));
        int p2 = jdd.ref( jdd.and(v1, v3));

        int [] os1 = jdd.oneSat(p1, null);
        assertEquals("onesat_v1 (1)", 1, os1[0]);
        assertEquals("onesat_v2 (1)", 0, os1[1]);
        assertEquals("onesat_v3 (1)", -1, os1[2]);


        os1 = jdd.oneSat(p2, null);
        assertEquals("onesat_v1 (2)", 1, os1[0]);
        assertEquals("onesat_v2 (2)", -1, os1[1]);
        assertEquals("onesat_v3 (2)", 1, os1[2]);
	}

	@Test public void testMember() {
		// TEST MEMBER: taken from the brace/rudell/bryant paper
		BDD jdd = new BDD(200);
		int v1 = jdd.createVar();
		int v2 = jdd.createVar();
		int v3 = jdd.createVar();

		int p1 = jdd.ref( jdd.and(v1,v2));
		int p2 = jdd.ref( jdd.or(v1,v2) );
		int p3 = jdd.ref( jdd.and(jdd.not(v1),v2) );
		int p4 = jdd.ref( jdd.and(jdd.not(v2),v1) );

		boolean []mb =  new boolean[]{ false, true};
		assertEquals("member (1)", false, jdd.member(p1, mb));
		assertEquals("member (2)", true, jdd.member(p2, mb));
		assertEquals("member (3)", true, jdd.member(p3, mb));
		assertEquals("member (4)", false, jdd.member(p4, mb));
	}
}

