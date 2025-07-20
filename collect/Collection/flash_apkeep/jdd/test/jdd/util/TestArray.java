package jdd.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestArray {

   @Test public void testArrayResizing() {
		int [] x = new int[]{2,3};
		int [] x2 = Array.resize(x, 2, 5);
		assertEquals("new array size OK", 5, x2.length);
		assertTrue("old data copied", x2[0] == 2 && x2[1] == 3);

		int [] x3 = Array.resize(x, 2, 1);
		assertEquals("new array size OK, even when decreasing", 1, x3.length);
    }


	// --- [test bed] ---------------------------------------------

   @Test public void testSet() {
		int [] x2 = new int[]{2,3, 0, 0, 0};		
		Array.set(x2, 5);
		for(int i = 0; i < x2.length; i++) assertEquals( "array set", 5, x2[i]);
	}

   @Test public void testCopy() {
		final int size = 1024;
		int [] a = new int[size];
		for(int i = 0; i < size; i++) a[i] = i;
		assertEquals("Im an idiot", 0, a[0]);

		Array.copy(a, a, size-1, 0, 1);
		assertEquals("Im still an idiot", 0, a[0]);
		assertEquals("backward_copy 1", 0, a[1]);
		assertEquals("backward_copy 2", size - 2, a[size-1]);

		Array.copy(a, a, size-1, 1, 0);
		assertEquals("forward_copy 1", 3, a[3]);
		assertEquals("forward_copy 2, not touched!", size - 2, a[size-1]);
		assertEquals("forward_copy 3, has been chagned back!", size - 2, a[size-2]);
	}
}
