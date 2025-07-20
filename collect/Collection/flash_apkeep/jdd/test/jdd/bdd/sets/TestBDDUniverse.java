
package jdd.bdd.sets;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.sets.*;

public class TestBDDUniverse {
	final float EPSILON = 0.0001f;

	@Test public void testAll() {
		int [] dum = { 3, 4, 5 , 1};
		BDDUniverse u = new BDDUniverse(dum);
		Set s1 = u.createEmptySet();
		Set s2 = u.createFullSet();

		// test trivial stuff
		assertEquals("Empty set has zero cardinality", s1.cardinality(), 0.0, EPSILON);
		assertEquals("Full set as large as the universe", s2.cardinality(), u.domainSize(), EPSILON);
		assertEquals("Single cardinality", u.cardinality(dum), 1, EPSILON);

		dum[0] = -1;
		assertEquals("DC leads to higher cardinality", u.cardinality(dum), 3, EPSILON);

/*
		// fill the vectors with junk
		for(int i = 0; i < 3; i++) { u.randomMember(dum); s1.insert(dum ); }
		s2.assign(s1);
		for(int i = 0; i < 3; i++) { u.randomMember(dum); s2.insert(dum ); }
		s1.show("S1");
		s2.show("S2");

		test simplify: choose s2 such that s1 <= s2 <= s3
		Set s3 = u.simplify(s2, s1);
		s3.show("S3");
		s3.free();
*/
		s1.free();
		s2.free();

	}
}
