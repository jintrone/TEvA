package edu.mit.cci.text.preprocessing;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * StopwordMunger Tester.
 *
 * @author <Authors name>
 * @since <pre>09/24/2012</pre>
 * @version 1.0
 */
public class StopwordMungerTest extends TestCase {
    public StopwordMungerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }



    /**
     *
     * Method: read(String from)
     *
     */
    public void testRead() throws Exception {
        String[] input = "The quick brown fox jumped over the lazy dog".split(" ");
        String[] expect ="The quick brown jumped over the".split(" ");
        StopwordMunger munger = new StopwordMunger(new String[]{"fox","lazy","dog"},false);
        List<String> result = new ArrayList<String>();
        for (String i:input) {
            if (munger.read(i)) {
                result.addAll(munger.flush());
            }
        }
        result.addAll(munger.finish());
        assertEquals(Arrays.asList(expect),result);

    }





    public static Test suite() {
        return new TestSuite(StopwordMungerTest.class);
    }
}
