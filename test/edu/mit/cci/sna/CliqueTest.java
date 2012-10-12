package edu.mit.cci.sna;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.teva.util.TevaUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;

/**
 * Clique Tester.
 *
 * @author <Authors name>
 * @since <pre>10/08/2012</pre>
 * @version 1.0
 */
public class CliqueTest extends TestCase {
    public CliqueTest(String name) {
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
     * Method: getId()
     *
     */
    public void testSerialization() throws Exception {
        Clique c = new Clique(1, Arrays.asList("one","two","three","four","five"));
        File f = new File("cliquetest.xml");

        TevaUtils.serialize(c,f);

        Clique c2 = TevaUtils.deserializeClique(f);

        assertEquals(c.getId(),c2.getId());
        assertEquals(c.getNodes(),c2.getNodes());
        assertEquals(c,c2);

    }




    public static Test suite() {
        return new TestSuite(CliqueTest.class);
    }
}
