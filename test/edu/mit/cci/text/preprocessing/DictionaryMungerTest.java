package edu.mit.cci.text.preprocessing;



import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DictionaryMunger Tester.
 *
 * @author <Authors name>
 * @since <pre>09/24/2012</pre>
 * @version 1.0
 */
public class DictionaryMungerTest extends TestCase {
    public DictionaryMungerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProcess() {
        String[][] replacements = new String[][] {
                {"one","two"},
                {"one","two","three"}
        };
        String[] input= "one little native two americans one two three did i stutter one".split(" ");
        String[] expect = "two little native two americans three three did i stutter two".split(" ");
        List<String> result = new ArrayList<String>();


        DictionaryMunger munger = new DictionaryMunger(replacements);
        for (String i:input) {
            if (munger.read(i)) {
              result.addAll(munger.flush());
            }
        }
        assertFalse(Arrays.asList(expect).equals(result));

        result.addAll(munger.finish());
        assertEquals(Arrays.asList(expect),result);

    }


    public static TestSuite suite() {
        return new TestSuite(DictionaryMungerTest.class);
    }
}
