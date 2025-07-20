package jdd.util.math;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestFastRandom {
	@Test public void testFastrandom() {
		final int MAX = 1000;
		java.util.Random rnd = new java.util.Random();

		int mt_c = 0, java_c = 0;
		for(int i = 0; i < 5; i++) {
			Chi2Test  mt_c2t = new Chi2Test(MAX);
			Chi2Test  java_c2t = new Chi2Test(MAX);
			while(mt_c2t.more()) {
				mt_c2t.add( FastRandom.mtrand() % MAX );
				java_c2t.add(rnd.nextInt(MAX));
			}
			if(!mt_c2t.isStandardDeviationAcceptable()) mt_c ++;
			if(!java_c2t.isStandardDeviationAcceptable()) java_c ++;
		}

		// max once in 5 tries
		assertTrue("Mersenne Twister PRNG working ok", mt_c <= 1);
		assertTrue("Java PRNG working ok", java_c <= 1);
	}
}
