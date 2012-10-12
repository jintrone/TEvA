package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityFrame;
import edu.mit.cci.teva.engine.DummyCommunityFrameHolder;
import edu.mit.cci.teva.util.TevaUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Community Tester.
 *
 * @author <Authors name>
 * @since <pre>10/08/2012</pre>
 * @version 1.0
 */
public class CommunityTest extends TestCase {
    public CommunityTest(String name) {
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
     * Method: isExpired()
     *
     */
    public void testSerialization() throws Exception {
        Community c = Community.create();

        CommunityFrame frame = new CommunityFrame(3);
        frame.add(new EdgeImpl(new NodeImpl("one"),new NodeImpl("two"),.5f,false));
        frame.add(new EdgeImpl(new NodeImpl("three"),new NodeImpl("four"),.7f,false));
        frame.add(new EdgeImpl(new NodeImpl("five"),new NodeImpl("six"),.8f,false));
        frame.addClique(new Clique(1, Arrays.asList("one", "two", "three")));

        CommunityFrame frame2 = new CommunityFrame(8);
        frame2.add(new EdgeImpl(new NodeImpl("seven"),new NodeImpl("eight"),.5f,false));
        frame2.add(new EdgeImpl(new NodeImpl("nine"),new NodeImpl("ten"),.7f,false));
        frame2.add(new EdgeImpl(new NodeImpl("eleven"),new NodeImpl("twelve"),.8f,false));
        frame2.addClique(new Clique(2, Arrays.asList("nine","ten","eleven")));
        c.addFrame(frame);
        c.addFrame(frame2);

        File output = new File("Community.xml");
         TevaUtils.serialize(output, c, Community.class);

        Community c2 = TevaUtils.deserialize(output,Community.class);
        assertEquals(c.id,c2.id);
        assertEquals(c.getMinBin(),c2.getMinBin());
        assertEquals(c.getMaxBin(),c2.getMaxBin());
        assertEquals(c.history.size(),c2.history.size());
        for (int key:c.history.keySet()) {
            CommunityFrame f1 = c.history.get(key);
            CommunityFrame f2 = c2.history.get(key);
            assertEquals(f1.cliques,f2.cliques);
            assertEquals(f1.getWindow(),f2.getWindow());
            assertEquals(f1.getCommunity().getId(),f2.getCommunity().getId());
            assertEquals(new HashSet<Edge>(f1.getEdges()),new HashSet<Edge>(f2.getEdges()));
        }

    }



    public static Test suite() {
        return new TestSuite(CommunityTest.class);
    }
}
