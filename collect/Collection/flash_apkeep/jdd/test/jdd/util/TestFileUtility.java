package jdd.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestFileUtility {

   @Test public void testFilename() {
		String []badnames = new String[]{ "", "abcd | xyz", "x > y", "x < y", "wait & see", "see ; wait" };
		for(String badname :badnames) {
			assertTrue("badname " + badname, FileUtility.invalidFilename(badname));
		}

		String []goodnames = new String[]{ "abcdxyz", "abcd.xyz" };
		for(String goodname :goodnames) {
			assertFalse("goodname " + goodname, FileUtility.invalidFilename(goodname));
		}

	}
}
