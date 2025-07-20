
package jdd.util;

import java.io.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class TestDot {
	private static final String TESTGRAPH = "graph { a -- b ; a -- c; }";

	@Test public void testBadFilename() {
		String []names = new String[] { "", "/idontexist/cantwritehere", "a|b", "../mamma-mia!"};

		Dot.setExecuteDot(false);
		for(String name : names){
			String filename = Dot.showString(name, "{}");
			assertEquals("bad filename ''" + name + "''", null, filename);
		}
	}

	@Test public void testExtension() {
		try {
			Dot.setExecuteDot(false);
			Dot.setType(Dot.TYPE_EPS);
			String filename1 = Dot.showString("graph", "{}");
			assertEquals("EPS filename", "graph.ps", filename1);

			Dot.setType(Dot.TYPE_PNG);
			String filename2 = Dot.showString("graph", "{}");
			assertEquals("PNG filename", "graph.png", filename2);
		} finally {
			FileUtility.delete("graph");
			FileUtility.delete("graph.ps");
			FileUtility.delete("graph.png");
		}
	}

	@Test public void testExecute() {
		try {
			Dot.setExecuteDot(true);
			Dot.setType(Dot.TYPE_PNG);
			Dot.setRemoveDotFile(false);

			String filename = Dot.showString("graph", TESTGRAPH);
			assertEquals("PNG filename", "graph.png", filename);

			File f = new File(filename);
			assertEquals("created PNG", true, f.exists());
		} finally {
			FileUtility.delete("graph");
			FileUtility.delete("graph.png");
		}
	}
}
