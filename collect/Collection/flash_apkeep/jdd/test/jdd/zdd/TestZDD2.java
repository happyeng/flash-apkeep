package jdd.zdd;

import org.junit.Test;
import static org.junit.Assert.*;

import jdd.util.*;
import jdd.bdd.*;

public class TestZDD2 {

	@Test public void testBasics() {

		ZDD2 zdd =  new ZDD2(1000);
		// Options.verbose = true; // want to see GC:s

		int a = zdd.createVar();
		int b = zdd.createVar();
		int c = zdd.createVar();
		int d = zdd.createVar();
		int e = zdd.createVar();
		int f = zdd.createVar();
		int g = zdd.createVar();
		int h = zdd.createVar();

        // MUL:
		// from minatos paper "Calculation of ..." {ab, b, c } * {ab, 1 } = {ab, abc, b, c }
		int tmp1 = zdd.cubes_union("010 100 011");
		int tmp2 = zdd.union( zdd.cube("11"), 1);
		int tmp3 = zdd.mul(tmp1, tmp2);
		int answer = zdd.cubes_union("010 100 011 111");
		assertEquals("P * Q", tmp3, answer);
		assertEquals("TOS restored after mul", zdd.debug_nstack_size(), 0);

		// simple mul test: prefix
		int mp = zdd.cubes_union( "011 111 1110");
		int md = zdd.cube("1000");
		int mpd= zdd.mul(mp, md);
		answer = zdd.cubes_union( "1011 1111 1110" );
		assertEquals("{ab,abc,bcd}*d = {abd,abcd,bcd}", mpd, answer);

		// suffix
		md = zdd.cube("1");
		mpd = zdd.mul(mp, md);
		answer = zdd.cubes_union( "011 111 1111" );
		assertEquals("{ab,abc,bcd}*a = {ab,abc,abc}", mpd, answer);

        // DIV
		// from minatos paper "ZBDDs and their applications" (p162)
		tmp1 = zdd.cubes_union( "111 110 101");
		tmp2 = zdd.cube("110");
		tmp3 = zdd.div(tmp1, tmp2);
		answer = zdd.union( zdd.cube("1"), 1);
		assertEquals("P / Q", tmp3, answer);
		assertEquals("TOS restored after div (1)", zdd.debug_nstack_size(), 0);


		// again, from minatos paper "ZBDDs and their applications" (bottom of p162)
		tmp1 = zdd.cubes_union("1011 10011 1000011 1100 10000100 10100");
		tmp2 = zdd.cubes_union("011 100");
		tmp3 = zdd.div(tmp1, tmp2);
		answer = zdd.cubes_union("1000 10000");
		assertEquals("P / Q (2)", tmp3, answer);
		assertEquals("TOS restored after div (2)", zdd.debug_nstack_size(), 0);
	}


    @Test public void testScalar() {
        // DIV & MOD: div/mod by a scalar, comparing with subset0/subset1
		ZDD2 zdd =  new ZDD2(1000);

        for(int i = 0; i < 10; i++)
			zdd.createVar();

        // prefix
        int tmp1   = zdd.cubes_union("1011 0111 1110");
        int tmp2   = zdd.cubes_union("1000");
        int answer = zdd.cubes_union("011 110");

        int tmp3 = zdd.div(tmp1, tmp2);
        assertEquals("div by scalar (prefix)", tmp3, answer);
        assertEquals("TOS restored after div (3)", zdd.debug_nstack_size(), 0);

        assertEquals("div by scalar (prefix)", answer, zdd.subset1(tmp1, zdd.getVar(tmp2)));

        tmp3 = zdd.mod(tmp1, tmp2);
        assertEquals("mod by scalar (prefix)", tmp3, zdd.subset0(tmp1, zdd.getVar(tmp2)));
        assertEquals("TOS restored after mod", zdd.debug_nstack_size(), 0);



        // suffix
        tmp1   = zdd.cubes_union("1011 0111 1110");
        tmp2   = zdd.cubes_union("0001");
        answer = zdd.cubes_union("1010 0110");

        tmp3 = zdd.div(tmp1, tmp2);
        assertEquals("div by scalar (suffix)", tmp3, answer);
        assertEquals("TOS restored after div (4)", zdd.debug_nstack_size(), 0);

        assertEquals("div by scalar (suffix)", answer, zdd.subset1(tmp1, zdd.getVar(tmp2)));

        tmp3 = zdd.mod(tmp1, tmp2);
        assertEquals("mod by scalar (suffix)", tmp3, zdd.subset0(tmp1, zdd.getVar(tmp2)));
        assertEquals("TOS restored after mod (2)", zdd.debug_nstack_size(), 0);



        // MUL+DIV+MOD
        // one generic mul/div/mod test
        tmp1   = zdd.cubes_union("0011 0111 1110");
        tmp2   = zdd.cubes_union("0110 0111");
        answer = zdd.cubes_union("0111 1111 1110");

        tmp3 = zdd.mul(tmp1, tmp2);
        assertEquals("generic mul", answer, tmp3);
        assertEquals("TOS restored after mul (2)", zdd.debug_nstack_size(), 0);

        assertEquals("generic div", 0,    zdd.div(tmp1, tmp2));
        assertEquals("generic mod", tmp1, zdd.mod(tmp1, tmp2));

	}

}

