
package jdd.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestFlags {

	@Test public void testSet() {
		Flags f = new Flags();

		f.set(0, true);
		f.set(1, true);
		f.set(1, false);
		f.set(2, true);
		assertEquals("get (1)", true, f.get(0));
		assertEquals("get (2)", false, f.get(1));
		assertEquals("get (3)", true, f.get(2));
	}

	@Test public void testSetAll() {
		Flags f = new Flags();		
	
		f.setAll(0);
		for(int i = 0; i < 32; i++)
			assertEquals("get FALSE (i)", false, f.get(i));

		f.setAll(-1);
		for(int i = 0; i < 32; i++)
			assertEquals("get TRUE (i)", true, f.get(i));

	}
}
