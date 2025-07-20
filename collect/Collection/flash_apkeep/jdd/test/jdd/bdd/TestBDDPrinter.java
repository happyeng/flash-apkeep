package jdd.bdd;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.*;

// we dont verify the output for these tests, just check that
// they dont crash

public class TestBDDPrinter {
	@Test public void testPrint() {
		try {
			BDD bdd = new BDD(100,10);
			bdd.createVars(4);

			int cube = bdd.ref( bdd.cube("01-1"));
			BDDPrinter.print(0, bdd);
			BDDPrinter.print(1, bdd);
			BDDPrinter.print(cube, bdd);

			BDDPrinter.printSet(0, 4, bdd, null);
			BDDPrinter.printSet(1, 4, bdd, null);
			BDDPrinter.printSet(cube, 5, bdd, null);
			BDDPrinter.printSet(cube, 5, bdd, new BDDNames());

		} catch(Error e) {
			fail("BDDPrinter internal error " + e.getMessage());
		}
	}

	@Test public void testPrintDot() {
		try {
			BDD bdd = new BDD(100,10);
			bdd.createVars(4);

			int cube = bdd.cube("1-01");
			BDDPrinter.printDot("test.dot", cube, bdd, new BDDNames());

		} catch(Error e) {
			fail("BDDPrinter internal error " + e.getMessage());
		} finally {
			FileUtility.delete("test.dot");
		}
	}
}


