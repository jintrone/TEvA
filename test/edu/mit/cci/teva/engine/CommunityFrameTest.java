package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.teva.util.TevaUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * CommunityFrame Tester.
 *
 * @author <Authors name>
 * @since <pre>10/08/2012</pre>
 * @version 1.0
 */
public class CommunityFrameTest extends TestCase {
    public CommunityFrameTest(String name) {
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
     * Method: getWindow()
     *
     */
    public void testSerialization() throws Exception {
        System.setProperty("jaxb.debug","true");

        CommunityFrame frame = new CommunityFrame(3);
        frame.add(new EdgeImpl(new NodeImpl("one"),new NodeImpl("two"),.5f,false));
        frame.add(new EdgeImpl(new NodeImpl("three"),new NodeImpl("four"),.7f,false));
        frame.add(new EdgeImpl(new NodeImpl("five"),new NodeImpl("six"),.8f,false));
        frame.addClique(new Clique(1, Arrays.asList("one","two","three")));
        File output = new File("CommunityFrame.xml");

        TevaUtils.serialize(output,new DummyCommunityFrameHolder(frame),DummyCommunityFrameHolder.class);

        CommunityFrame frame2 = ((DummyCommunityFrameHolder)TevaUtils.deserialize(output,DummyCommunityFrameHolder.class)).getFrame();
        assertEquals(frame.getCliques(),frame2.getCliques());
        assertEquals(new HashSet<Edge>(frame.getEdges()), new HashSet<Edge>(frame2.getEdges()));
        assertEquals(frame.getWindow(),frame2.getWindow());

    }



    public static Test suite() {
        return new TestSuite(CommunityFrameTest.class);
    }
}
