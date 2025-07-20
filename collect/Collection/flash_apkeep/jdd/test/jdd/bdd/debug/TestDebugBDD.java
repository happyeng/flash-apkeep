package jdd.bdd.debug;

import jdd.util.*;
import jdd.util.math.*;

import org.junit.Test;
import static org.junit.Assert.*;


// For testing, we need a DebugBDD version that does not call exit
class DebugBDDNonFatal extends DebugBDD {
	public int errorCount = 0;
	public DebugBDDNonFatal(int a, int b) { super(a, b); }
	public void fatal(Error e, String message) {
		errorCount ++;
	}
}
public class TestDebugBDD {

	@Test public void testRefCount() {

		DebugBDDNonFatal dbdd = new DebugBDDNonFatal(1000, 100);

		int v1 = dbdd.createVar();
		int v2 = dbdd.createVar();
		int v3 = dbdd.createVar();

		int a = dbdd.and(v1,v2);

		assertEquals("no errors before ref-count error", 0, dbdd.errorCount );

		int b = dbdd.and(a, v3);
		assertEquals("one error before ref-count error", 1, dbdd.errorCount );
	}
}

