package edu.mit.cci.text.preprocessing;

import edu.mit.cci.text.preprocessing.CompositeMunger;
import edu.mit.cci.text.preprocessing.MockMunger;
import edu.mit.cci.text.preprocessing.Munger;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * CompositeMunger Tester.
 *
 * @author <Authors name>
 * @since <pre>09/23/2012</pre>
 * @version 1.0
 */
public class CompositeMungerTest extends TestCase {
    public CompositeMungerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }



    public void testProcess() throws Exception {
        String[] input = "The quick brown fox jumped over the lazy dog".split(" ");
        List<String> output = new ArrayList<String>();
        Munger[] mungers = new Munger[3];
        Random r = new Random();
        for (int i = 0;i<3;i++) {
            mungers[i] = new MockMunger(r.nextInt(4));
        }
        CompositeMunger munger = new CompositeMunger(mungers);
        for (String i:input) {
            if (munger.read(i)) {
                output.addAll(munger.flush());
            }
        }
        output.addAll(munger.flush());

        assertEquals("Input and output lists should be identical", Arrays.asList(input),output);


    }

    public void testFlush() throws Exception {
        //TODO: Test goes here...
    }





    public static Test suite() {
        return new TestSuite(CompositeMungerTest.class);
    }
}
