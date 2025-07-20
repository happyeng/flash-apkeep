package jdd.util.math;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestDigits {

	@Test public void testLog2Ceil() {
		assertEquals("log2_ceil (1)", 2, Digits.log2_ceil(3));
		assertEquals("log2_ceil (2)", 2, Digits.log2_ceil(4));
		assertEquals("log2_ceil (3)", 4, Digits.log2_ceil(10));
		assertEquals("log2_ceil (4)", 4, Digits.log2_ceil(16));
		assertEquals("log2_ceil (5)", 5, Digits.log2_ceil(17));
	}

	@Test public void testClosestLog2() {
		assertEquals("closest_log2 (1)", 2, Digits.closest_log2(5));
		assertEquals("closest_log2 (2)", 2, Digits.closest_log2(4));
		assertEquals("closest_log2 (3)", 3, Digits.closest_log2(7));
		assertEquals("closest_log2 (4)", 4, Digits.closest_log2(16));
		assertEquals("closest_log2 (5)", 4, Digits.closest_log2(17));
		assertEquals("closest_log2 (6)", 5, Digits.closest_log2(31));
	}


	@Test public void testPrettify() {
		assertEquals("prettify(500)", "500", Digits.prettify(500));
		assertEquals("prettify(1200)", "1.20K", Digits.prettify(1200));
		assertEquals("prettify(5555)", "5.56K", Digits.prettify(5555));
		assertEquals("prettify(5555555)", "5.56M", Digits.prettify(5555555));
	}


	@Test public void testPrettify1024() {
		assertEquals("prettify1024(500)", "500", Digits.prettify1024(500));
		assertEquals("prettify1024(1200)", "1.17K", Digits.prettify1024(1200));
	}
}
