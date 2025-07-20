package jdd.util.math;

import jdd.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestPrime {


	private static boolean dumb_prime_check(int n) {
		int n0 = (int) Math.sqrt(n);
		if(n == 0) return false;
		if(n == 1) return true;
		for(int i = 2; i <= n0; i++) if( (n % i) == 0) return false;
		return true;
	}

	private static int dumb_next_prime(int n) {
		for(;;) if(dumb_prime_check(n)) return n; else n++;
	}

	@Test public void testSimple() {
		assertTrue("1 is prime", Prime.isPrime(1));
		assertTrue("2 is prime", Prime.isPrime(2));
		assertTrue("3 is prime", Prime.isPrime(3));
		assertTrue("4 is NOT prime", !Prime.isPrime(4));
		assertTrue("5 is prime", Prime.isPrime(5));
		assertTrue("6 is NOT prime", !Prime.isPrime(6));
		assertTrue("7 is prime", Prime.isPrime(7));
		assertTrue("8 is NOT prime", !Prime.isPrime(8));
		assertTrue("256 is NOT prime", !Prime.isPrime(256));
		assertTrue("13221 is NOTprime", !Prime.isPrime(13221));
	}

	@Test public void testRandom() {
		boolean failed = false;
		for(int i = 0; !failed && i < 3000; i++) {
			int n = (int)(Math.random() * 1234567);
			if( Prime.isPrime(n) != dumb_prime_check(n))
				failed = true;
		}
		assertTrue("Prime.isPrime failed", !failed);
	}

	@Test public void testRandomNext() {
		boolean failed = false;
		for(int i = 0; !failed && i < 3000; i++) {
			int n = (int)(Math.random() * 1234567);
			if(Prime.nextPrime(n) != dumb_next_prime(n)) 
				failed = true;
		}
		assertTrue("nextPrime failed", !failed);
	}
}
