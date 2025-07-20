package jdd.zdd;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.*;
import jdd.bdd.*;

public class TestZDDCSP {

	@Test public void testAll() {
		ZDDCSP csp = new ZDDCSP(200, 1000);
		int a = csp.createVar();
		int b = csp.createVar();
		int c = csp.createVar();
		int d = csp.createVar();
		int e = csp.createVar();

        int p = csp.cubes_union("00011 00111 01110");
        int q = csp.cubes_union("00110 00111");

		// test P * Q
		int answer_product = csp.cubes_union("00111 01111 01110");
		assertEquals("P * Q", answer_product, csp.mul(p,q) );
		assertEquals("TOS restored (1)", csp.debug_nstack_size(), 0 );


		// test P / Q
		int answer_division = 0;
		assertEquals("P / Q", answer_division, csp.div(p,q) );
		assertEquals("TOS restored (2)", csp.debug_nstack_size(), 0);


		// test P % Q
		int answer_quotient = csp.cubes_union("00011 00111 01110");
		assertEquals("P % Q", answer_quotient, csp.mod(p,q));
		assertEquals("TOS restored (3)", csp.debug_nstack_size(), 0);



		// restrict:
		int answer_restrict_pq = csp.cubes_union("00111 01110");
		int answer_restrict_qp = csp.cube("00111");
		assertEquals("Restrict(P,Q)", csp.restrict(p, q), answer_restrict_pq);
		assertEquals("Restrict(Q,P)", csp.restrict(q, p), answer_restrict_qp);
		assertEquals("TOS restored (4)", csp.debug_nstack_size(), 0);


		// exclude
		int answer_exclude_pq = csp.cube("00011");
		int answer_exclude_qp = csp.cube("00110");
		assertEquals("Exclude(P,Q)", csp.exclude(p, q), answer_exclude_pq);
		assertEquals("Exclude(Q,P)", csp.exclude(q, p), answer_exclude_qp);
		assertEquals("TOS restored (5)", csp.debug_nstack_size(), 0);
	}
}

