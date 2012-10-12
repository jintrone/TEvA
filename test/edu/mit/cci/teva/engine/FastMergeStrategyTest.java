package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.sna.Node;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.File;
import java.util.HashSet;

/**
 * FastMergeStrategy Tester.
 *
 * @author <Authors name>
 * @since <pre>10/11/2012</pre>
 * @version 1.0
 */
public class FastMergeStrategyTest extends TestCase {
    public FastMergeStrategyTest(String name) {
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
     * Method: process(Network from, Collection<CommunityFrame> cdfrom, Network to, Collection<CommunityFrame> cdto)
     *
     */
    public void testWeakComponentClusterer() throws Exception {
        Network one = NetworkUtils.readNetworkFile(new File("debug12.net"));
        Network two = NetworkUtils.readNetworkFile(new File("debug19.net"));

        assertEquals(one.getEdges().size(),two.getEdges().size());
        assertEquals(one.getNodes().size(),two.getNodes().size());
        assertEquals(new HashSet<Node>(one.getNodes()),new HashSet<Node>(two.getNodes()));
        assertEquals(one.getEdges(),two.getEdges());


    }


    public static Test suite() {
        return new TestSuite(FastMergeStrategyTest.class);
    }
}
