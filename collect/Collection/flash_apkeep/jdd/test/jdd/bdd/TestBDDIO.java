package jdd.bdd;

import java.io.*;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.*;

public class TestBDDIO {
	final float EPSILON = 0.001f;

	@Test public void testLoadSave() {
		try {
			BDD bdd = new BDD(100,10);
			int v1 = bdd.createVar();
			int v2 = bdd.createVar();
			int v3 = bdd.createVar();
			int v4 = bdd.createVar();

			int test = bdd.cube("1-01");
			BDDIO.save(bdd, test, "test.bdd");
			double sat = bdd.satCount(test);
			int nodes = bdd.nodeCount(test);

			BDD bdd2 = new BDD(1,10); // force GC in the middle of job

			int x = BDDIO.load(bdd2, "test.bdd");
			assertEquals("sat-count (1)", sat, bdd2.satCount(x), EPSILON);
			assertEquals("node-count (1)", nodes, bdd2.nodeCount(x), EPSILON);

			BDDIO.save(bdd2, x, "test.bdd");
			int x2 = BDDIO.load(bdd, "test.bdd");
			assertEquals("BDD consistency failed", test, x2);

		} catch(IOException exx) {
			assertTrue("EXCEPTION CAUGHT: " + exx.getMessage(), false);
		} finally {
			// and cleanup...
			FileUtility.delete("test.bdd");
		}
	}

	@Test public void testBuddy() {
		// TODO: how do we test saveBuDDy ???
	}
}

