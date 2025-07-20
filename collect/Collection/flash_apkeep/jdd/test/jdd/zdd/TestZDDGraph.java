package jdd.zdd;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.*;
import jdd.util.math.*;

import jdd.bdd.*;

public class TestZDDGraph    {

	@Test public void testAll() {

		ZDDGraph g = new ZDDGraph(2000, 10000);
		int v1 = g.createVar();
		int v2 = g.createVar();
		int v3 = g.createVar();
		int v4 = g.createVar();
		int v5 = g.createVar();


        int x = g.cubes_union("00011 00010 00110");
        int y = g.cubes_union("00011 00010 10011 00101");
        int p = g.cubes_union("00011 00111 01110");
        int q = g.cubes_union("00110 00111");


		// test no-subset
		int ns1 = g.noSubset(x,y);
		int ns2 = g.noSubset(y,x);
        int ns3 = g.noSubset(p,q);
        int ns4 = g.noSubset(q,p);
        int ns1_answer = g.cube("00110");
        int ns2_answer = g.cubes_union("00101 10011");
        int ns3_answer = g.cube("01110");
        int ns4_answer = 0;
		assertEquals("noSubset(X,Y)", ns1, ns1_answer);
		assertEquals("noSubset(Y,X)", ns2, ns2_answer);
        assertEquals("noSubset(P,Q)", ns3, ns3_answer);
		assertEquals("noSubset(Q,P)", ns4, ns4_answer);
		assertEquals("nstack_tos restored (1)", g.debug_nstack_size(), 0);



		// test no-superset
		int ns5 = g.noSupset(x,y);
		int ns6 = g.noSupset(y,x);
        int ns7 = g.noSupset(p,q);
		int ns8 = g.noSupset(q,p);
        int ns5_answer = 0;
        int ns6_answer = g.cube("00101");
        int ns7_answer = g.cube("00011");
        int ns8_answer = g.cube("00110");
		assertEquals("noSupset(X,Y)", ns5,  ns5_answer);
		assertEquals("noSupset(Y,X)", ns6,  ns6_answer);
        assertEquals("noSupset(P,Q)", ns7,  ns7_answer);
		assertEquals("noSupset(Q,P)", ns8,  ns8_answer);
		assertEquals("nstack_tos restored (2)", g.debug_nstack_size(), 0);


		// test maxset:
		int ms1 = g.maxSet(x);
		int ms2 = g.maxSet(y);
		assertEquals("maxSet (1)", ms1, g.union( g.cube("11"), g.cube("110") ));
		assertEquals("maxSet (2)", ms2, g.union( g.cube("10011"), g.cube("101") ));
		assertEquals("nstack_tos restored (3)", g.debug_nstack_size(), 0);


		// test allEdge:
		assertEquals("allEdge(1)", g.count( g.allEdge(v1,v2)), Digits.maxUniquePairs(v2-v1+1));
		assertEquals("allEdge(2)", g.count( g.allEdge(v1,v3)), Digits.maxUniquePairs(v3-v1+1));
		assertEquals("allEdge(3)", g.count( g.allEdge(v1,v4)), Digits.maxUniquePairs(v4-v1+1));
		assertEquals("allEdge(4)", g.count( g.allEdge(v1,v5)), Digits.maxUniquePairs(v5-v1+1));
	}

}

