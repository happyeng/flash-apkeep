package jdd.util.math;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestHashFunctions {

	@Test public void testHash() {
		// this simple hash-function testbed looks at the distribution of hashes of random numbers
		// using an standard \Chi^2 test. Of course, the randoms numbers themselves sometimes fail
		// the chi^2 test, so this testbed is really not very accurate.
		
		int table_size = 10000;

		// get the tester objects
		Chi2Test c2t[] = new Chi2Test[7];
		for(int i = 0; i < c2t.length; i++) c2t[i] = new Chi2Test(table_size);

		// and get random hashes until we have enough to do a chi^2 test!
		do {
			// first, we need some random numbers
			int rnd3 = FastRandom.mtrand() % table_size;
			int rnd2 = FastRandom.mtrand() % table_size;
			int rnd1 = FastRandom.mtrand() % table_size;

			c2t[0].add(rnd1); // the primes themself should be tested too :)

			// pair hashes
			int hp2 = HashFunctions.hash_pair(rnd1, rnd2);
			int hp3 = HashFunctions.hash_pair(rnd1, rnd2, rnd3);
			c2t[1].add( (0x7FFFFFFF & HashFunctions.mix(hp2)) % table_size );
			c2t[2].add( (0x7FFFFFFF & HashFunctions.mix(hp3)) % table_size );

			// prime hashes
			int hr2 = HashFunctions.hash_prime(rnd1, rnd2);
			int hr3 = HashFunctions.hash_prime(rnd1, rnd2, rnd3);
			c2t[3].add( (0x7FFFFFFF & HashFunctions.mix(hr2)) % table_size );
			c2t[4].add( (0x7FFFFFFF & HashFunctions.mix(hr3)) % table_size );

			// jenkins
			int hj3 = HashFunctions.hash_jenkins(rnd1, rnd2, rnd3);
			c2t[5].add( (0x7FFFFFFF & HashFunctions.mix(hj3)) %  table_size );

			// FNV
			int hfnv3 = HashFunctions.hash_FNV(rnd1, rnd2, rnd3);
			c2t[6].add( (0x7FFFFFFF & HashFunctions.mix(hfnv3)) %  table_size );

		}  while(c2t[0].more());


		// check if the random number itself was good enough!
		assertTrue("FastRandom.mtrand() has an unacceptable standard deviation" , c2t[0].isStandardDeviationAcceptable());

		// check the hash distribution
		// the error rate we accept. this should actually be 3.0 for true RNG!
		final double MAX_ERROR = 5.0;
		for(int i = 5; i < c2t.length; i++) {
			assertTrue( "Standard Deviation not acceptable for hash " + i, Math.abs(c2t[i].getStandardDeviation()) < MAX_ERROR);
		}
	}
}
