
package jdd.bdd.sets;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.sets.*;

public class TestBDDSet {
	final float EPSILON = 0.0001f;

	@Test public void testAll() {
		final int [] dum = { 3, 4, 5 , 2};
		BDDUniverse u = new BDDUniverse(dum);
		Set s1 = u.createEmptySet();
		Set s2 = u.createFullSet();

		// test insert, remove and member
		int [] v = new int[4];
		v[0] = v[1] = v[2] = v[3] = 0;

		assertTrue("v not in S1 before", s1.insert(v));
		assertTrue("v in S1 after", !s1.insert(v));
		assertEquals("Cardinality 1 after inserting v", s1.cardinality(), 1.0, EPSILON);
		assertTrue("v \\in S1", s1.member(v));
		assertTrue("v removed from S1", s1.remove(v));
		assertTrue("v \\not\\in S1", !s1.member(v));
		assertTrue("v already removed from S1 and not in S1 anymore", !s1.remove(v));
		assertEquals("S1 empty again", s1.cardinality(), 0.0, EPSILON);

		// check empty and clear:
		assertTrue("S1 is empty", s1.isEmpty());
		assertTrue("S2 is not empty", !s2.isEmpty());

		// test invert
		Set s1_neg = s1.invert();
		assertTrue("(NOT  emptyset) = fullset", s1_neg.equals( s2));
		s1_neg.free();

		// test copy:
		Set s2_copy = s2.copy();
		assertTrue("copy() test", s2_copy.equals( s2));

		// ...and clear
		s2_copy.clear();
		assertTrue("clear() test", s2_copy.equals( s1));
		s2_copy.free();

		// check union
		Set x0 = u.createEmptySet();
		Set x1 = u.createEmptySet();
		Set x10 = u.createEmptySet();

		v[0] = v[1] = v[2] = v[3] = 0; x0.insert(v); x10.insert(v);
		v[0] = v[1] = v[2] = v[3] = 1; x1.insert(v); x10.insert(v);
		Set union = x1.union(x0);
		assertTrue("union() - test", union.equals( x10));
		union.free();

		// check diff:
		Set diff1 = x10.diff( x1);
		Set diff2 = x10.diff( x0);
		assertTrue("diff() - test 1", diff1.equals( x0));
		assertTrue("diff() - test 2", diff2.equals( x1));
		diff1.free();
		diff2.free();

		// check intersection
		Set int1 = x10.intersection( x1);
		Set int2 = x10.intersection( x0);
		assertTrue("intersection() - test 1", int1.equals( x1));
		assertTrue("intersection() - test 2", int2.equals( x0));
		int1.free();
		int2.free();

		// check compare:
		assertEquals("x1 = x1", x1.compare(x1), 0);
		assertEquals("x1  < x10", x10.compare(x1), +1);
		assertEquals("x10 > x1", x1.compare(x10), -1);

		assertEquals("x10 > x0", x10.compare(x0), +1);
		assertEquals("x0  < x0", x0.compare(x10), -1);
		assertEquals("x10 ?? x0", x1.compare(x0), Integer.MAX_VALUE); // no relation

		s1.free();
		s2.free();
	}
}
