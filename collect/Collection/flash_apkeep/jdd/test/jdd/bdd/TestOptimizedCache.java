package jdd.bdd;


import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.*;
import jdd.util.math.*;

public class TestOptimizedCache {

	@Test public void testThree() {
		OptimizedCache cache = new OptimizedCache("test", 200, 3,3);

		cache.add(2, 1,2,3);
		assertTrue("lookup 3", cache.lookup( 2,1,2) && cache.answer == 3);

		cache.add(2, 1,2,5);
		assertTrue("lookup overwritten with 5",  cache.lookup( 2,1,2) && cache.answer == 5);
		assertTrue("non-existing entry 1", !cache.lookup( 1,1,2));
		assertTrue("non-existing entry 2", !cache.lookup( 2,2,2));
		assertTrue("non-existing entry 3", !cache.lookup( 2,2,1));
	}

	@Test public void testTwo() {
		OptimizedCache cache = new OptimizedCache("test", 200, 2,2);

		cache.add(2, 1,3);
		assertTrue("lookup 3", cache.lookup( 2,1) && cache.answer == 3);

		cache.add(2, 1,5);
		assertTrue("lookup overwritten with 5",  cache.lookup( 2,1) && cache.answer == 5);
		assertTrue("non-existing entry 1", !cache.lookup( 1,1));
		assertTrue("non-existing entry 2", !cache.lookup( 2,2));
	}

	@Test public void testOne() {
		OptimizedCache cache = new OptimizedCache("test", 200, 1,1);

		cache.add(1,3);
		assertTrue("lookup 3", cache.lookup(1) && cache.answer == 3);

		cache.add(1,5);
		assertTrue("lookup overwritten with 5", cache.lookup(1) && cache.answer == 5);
		assertTrue("non-existing entry 1", !cache.lookup(2));
		assertTrue("non-existing entry 2", !cache.lookup(3));
	}
}
