package jdd.examples;


import org.junit.Test;
import static org.junit.Assert.*;

public class TestBDDQueens
{
	@Test public void testSolutions() {
		int [] correct = { 1, 0,0,2, 10, 4, 40,  92 /*,  352, 724 */ };
		for(int i = 0; i < correct.length; i++) {
			BDDQueens q = new BDDQueens( i + 1 );
			assertEquals("correct solutions for " + (i + 1) + " queens", q.numberOfSolutions(), correct[i], 0.0001f);
		}

	}
}
