package jdd.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestAllocator {

   @Test public void testStats() {

		// needed for our CI, but should not really be needed with proper test isolation
		Allocator.resetStats();

		assertEquals("No allocation at begining",
			0, Allocator.getStatsCount(Allocator.TYPE_INT));

		Allocator.allocateIntArray(10);
		Allocator.allocateIntArray(12);
		Allocator.allocateIntArray(8);
		Allocator.showStats();

		assertEquals("int allocator stats: count",
			3, Allocator.getStatsCount(Allocator.TYPE_INT));
		assertEquals("int allocator stats: size",
			30, Allocator.getStatsTotal(Allocator.TYPE_INT));
		assertEquals("int allocator stats: max",
			12, Allocator.getStatsMax(Allocator.TYPE_INT));

			assertEquals("int allocator stats: total bytes",
			30 * 4, Allocator.getStatsTotalBytes());
    }


}
