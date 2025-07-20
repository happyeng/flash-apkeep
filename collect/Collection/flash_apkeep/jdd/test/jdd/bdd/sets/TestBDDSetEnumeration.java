
package jdd.bdd.sets;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.sets.*;

public class TestBDDSetEnumeration {
	final float EPSILON = 0.0001f;

	@Test public void testAll() {
		int [] dom = { 10,20,30, 40, 50, 60 };
		BDDUniverse u = new BDDUniverse(dom);
		Set set = u.createEmptySet();

		int [] val = new int[dom.length];
		int real_size = 0;
		for(int i = 0; i < 200; i++) {
			for(int j = 0; j < dom.length; j++) val[j] = (int)( Math.random() * dom[j]);
			if(set.insert(val)) real_size++;
		}

		assertEquals("# of elemnets inserted equals set cardinality", real_size, set.cardinality(), EPSILON);


		Set set2 = set.copy();
		SetEnumeration se = set.elements();
		int had = 0;
		while(se.hasMoreElements() ) {
			had++;
			int [] v = se.nextElement();
			assertTrue("returned element really in set", set2.remove(v) );
		}

		assertEquals("# of elemnets inserted still equals set cardinality", real_size, set.cardinality(), EPSILON);
		assertEquals("right number of elements in set", had, set.cardinality(), EPSILON);
		assertEquals("right number of elements in set (Same as above)", 0, set2.cardinality(), EPSILON);

		set2.free();
		set.free();
		se.free();

	}
}
