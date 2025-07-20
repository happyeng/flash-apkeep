package jdd.bdd;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestNodeTable {

	@Test public void testSize() {
		NodeTable nt = new NodeTable(10);
		NodeTableChecker ntc = new NodeTableChecker(nt);

		nt.add(4,0,1);
		assertEquals("Table ok after grow", nt.debug_table_size(), nt.debug_free_nodes_count() + 3 );
		assertEquals("Nodetable check", null, ntc.check());
	}

	@Test public void testGrow() {
		final int MAX = 15;

		NodeTable nt = new NodeTable(MAX);
		NodeTableChecker ntc = new NodeTableChecker(nt);

		int last = 0;
		for(int i = 2; i < MAX; i++)
			last = nt.add(i , last, last);

		for(int i = 0; i < 5; i++) {
			assertEquals("Nodetable check " + i, null, ntc.check());
			nt.grow();
		}
	}

	@Test public void testHash() {
		NodeTable nt = new NodeTable(10);

		// save by nstack
		int a = nt.add(4,0,1);
		nt.nstack.push(a);

		// save by ref
		int b = nt.add(4,1,0);
		nt.ref(b);

		// dont save:
		int c = nt.add(3,0,1);
		assertEquals("free node count correct (1)",  nt.debug_compute_free_nodes_count(), nt.debug_free_nodes_count() );

		nt.gc();
		assertTrue("saved by nstack", nt.isValid( a));
		assertTrue("saved by ref", nt.isValid( b));
		assertTrue("should have been removed", !nt.isValid( c));
		assertEquals("free node count correct (2)",  nt.debug_compute_free_nodes_count(), nt.debug_free_nodes_count() );

		nt.grow();
		assertTrue("saved by nstack", nt.isValid( a));
		assertTrue("saved by ref", nt.isValid( b));
		assertTrue("should have been removed", !nt.isValid( c));
		assertEquals("free node count correct (3)",  nt.debug_compute_free_nodes_count(), nt.debug_free_nodes_count() );
	}
}
