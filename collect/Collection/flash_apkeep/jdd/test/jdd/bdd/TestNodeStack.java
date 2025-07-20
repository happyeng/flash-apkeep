package jdd.bdd;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestNodeStack {

	@Test public void testPushPop() {
		NodeStack ns = new NodeStack(10);

		assertEquals("Start empty", ns.getTOS(), 0);

		int n = ns.push(123);
		assertEquals("Stack has one", ns.getTOS(), 1);
		assertEquals("Stack push return", n, 123);

		ns.push(456);
		ns.push(678);
		ns.push(910);
		assertEquals("Stack has four", ns.getTOS(), 4);

		n = ns.pop();
		assertEquals("Stack has three", ns.getTOS(), 3);
		assertEquals("Stack pop", n, 910);

		ns.drop(2);
		n = ns.pop();
		assertEquals("Stack has zero", ns.getTOS(), 0);
		assertEquals("Stack pop (2)", n, 123);
	}

	@Test public void testGrow() {
		NodeStack ns = new NodeStack(10);

		int s = ns.getCapacity();
		assertTrue("Initial size", s >= 10);

		ns.push(123);
		ns.push(456);

		ns.grow(s + 10);
		int s2 = ns.getCapacity();
		assertTrue("Grown size", s2 >= s + 10);
		assertEquals("TOS correct after grow", ns.getTOS(), 2);

		int n1 = ns.pop();
		int n2 = ns.pop();
		assertEquals("pop 1 after grow", n1, 456);
		assertEquals("pop 2 after grow", n2, 123);
		assertEquals("TOS correct after pop", ns.getTOS(), 0);
	}


	@Test public void testReset() {
		NodeStack ns = new NodeStack(10);

		ns.push(123);
		ns.push(567);
		assertEquals("TOS after push", ns.getTOS(), 2);

		ns.reset();
		assertEquals("TOS after reset", ns.getTOS(), 0);
	}
}
