package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.teva.util.TevaUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

/**
 * CommunityModel Tester.
 *
 * @author <Authors name>
 * @since <pre>10/09/2012</pre>
 * @version 1.0
 */
public class CommunityModelTest extends TestCase {
    public CommunityModelTest(String name) {
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
     * Method: addConnection(int bin, float weight, ConnectionType type, Community from, Community to)
     *
     */
    public void testSerialization() throws Exception {
       Community c = Community.create();

        CommunityFrame frame = new CommunityFrame(3);
        frame.add(new EdgeImpl(new NodeImpl("one"),new NodeImpl("two"),.5f,false));
        frame.add(new EdgeImpl(new NodeImpl("three"),new NodeImpl("four"),.7f,false));
        frame.add(new EdgeImpl(new NodeImpl("five"),new NodeImpl("six"),.8f,false));
        frame.addClique(new Clique(1, Arrays.asList("one", "two", "three")));
        c.addFrame(frame);

        CommunityFrame frame1 = new CommunityFrame(8);
        frame1.add(new EdgeImpl(new NodeImpl("seven"),new NodeImpl("eight"),.5f,false));
        frame1.add(new EdgeImpl(new NodeImpl("nine"),new NodeImpl("ten"),.7f,false));
        frame1.add(new EdgeImpl(new NodeImpl("eleven"),new NodeImpl("twelve"),.8f,false));
        frame1.addClique(new Clique(2, Arrays.asList("nine","ten","eleven")));
        c.addFrame(frame1);

        Community c1 = Community.create();

        CommunityFrame frame2 = new CommunityFrame(4);
        frame2.add(new EdgeImpl(new NodeImpl("one"),new NodeImpl("two"),.5f,false));
        frame2.add(new EdgeImpl(new NodeImpl("three"),new NodeImpl("four"),.7f,false));
        frame2.add(new EdgeImpl(new NodeImpl("five"),new NodeImpl("six"),.8f,false));
        frame2.addClique(new Clique(1, Arrays.asList("one", "two", "three")));
        c1.addFrame(frame2);

        CommunityFrame frame3 = new CommunityFrame(9);
        frame3.add(new EdgeImpl(new NodeImpl("seven"),new NodeImpl("eight"),.5f,false));
        frame3.add(new EdgeImpl(new NodeImpl("nine"),new NodeImpl("ten"),.7f,false));
        frame3.add(new EdgeImpl(new NodeImpl("eleven"),new NodeImpl("twelve"),.8f,false));
        frame3.addClique(new Clique(2, Arrays.asList("nine","ten","eleven")));
        c1.addFrame(frame3);


        CommunityModel model = new CommunityModel(new TevaParameters(),new Date[][] {
                {new Date(),new Date()},
                {new Date(),new Date()}},"TestCorpus");
        model.addCommunity(c);
        model.addCommunity(c1);

        model.addConnection(3,.4f, CommunityModel.ConnectionType.INFORMS,c,c1);
        model.addConnection(9,.8f, CommunityModel.ConnectionType.SPAWNS,c1,c);

        File output = new File("CommunityModel.xml");
        TevaUtils.serialize(output,model,CommunityModel.class, HashSet.class);

        CommunityModel model1 = TevaUtils.deserialize(output,CommunityModel.class,HashSet.class);

        for (int i=0;i<model.windows.length;i++) {
            assertEquals(model.windows[i][0],model1.windows[i][0] );
            assertEquals(model.windows[i][1],model1.windows[i][1] );
        }

        assertTrue(model.spawners.equals(model1.spawners));
        assertEquals(model.consumers,model1.consumers);


    }


    public static Test suite() {
        return new TestSuite(CommunityModelTest.class);
    }
}
