package edu.mit.cci.sna.jung;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.text.wordij.LinearWeightNetworkGenerator;
import edu.uci.ics.jung.graph.Graph;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * JungUtils Tester.
 *
 * @author <Authors name>
 * @since <pre>09/26/2012</pre>
 * @version 1.0
 */
public class JungUtilsTest extends TestCase {
    public JungUtilsTest(String name) {
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
     * Method: copy(AbstractGraph<NodeImpl,EdgeImpl> graph, boolean directed)
     *
     */
    public void testMergeAddMax1() throws Exception {
        String[] sample1 = new String[]{"1","2","3","4","5","3","4","5","6","7"};
        String[] sample2 = new String[]{"3","4","5","6","7","5","6","7","8","9"};
        LinearWeightNetworkGenerator generator = new LinearWeightNetworkGenerator(3,2);
        Network one = generator.calculateWeights(Arrays.asList(sample1));
        Network two = generator.calculateWeights(Arrays.asList(sample2));

        JungUtils.merge((Graph< Node,Edge>)one,(Graph< Node,Edge>)two, JungUtils.MergePolicy.ADD_MAX_1);
        //weight 1
        int[][] edges_1 = new int[][] {
                {1,2},
                {2,3},
                {3,4},
                {3,5},
                {4,5},
                {4,6},
                {5,6},
                {5,7},
                {6,7},
                {7,8},
                {8,9}
        };
        ////weight .99
        int[][] edges_9 = new int[][] {
                {3,6},
                {4,7}
        };
        //weight .66
        int[][] edges_6 = new int[][]{
                {1,3},
                {2,4},
                {2,5},
                {3,7},
                {5,8},
                {6,8},
                {7,9}
        };
        //weight .33
        int[][] edges_3 = new int[][]{
                {1,5},
                {1,4},
                {2,6},
                {4,8},
                {5,9},
                {6,9}

        };

        assertEquals("Wrong number of edges in merged graph",edges_1.length+edges_3.length+edges_6.length+edges_9.length,one.getEdges().size());
        for (int[] edge:edges_1) {
            Node n1 = new NodeImpl(edge[0]+"");
            Node n2 = new NodeImpl(edge[1]+"");
            Edge e = ((Graph<Node, Edge>) one).findEdge(n1,n2);
            assertNotNull(e);
            assertEquals("Incorrect weight for "+n1+"-"+n2,1.0f,e.getWeight(),.0001);
        }

        for (int[] edge:edges_9) {
            Node n1 = new NodeImpl(edge[0]+"");
            Node n2 = new NodeImpl(edge[1]+"");
            Edge e = ((Graph<Node, Edge>) one).findEdge(n1,n2);
            assertNotNull(e);
            assertTrue("Weight should be greater than .99 "+n1+"-"+n2,e.getWeight()>.99f);
            assertTrue("Weight should be less than 1.0 "+n1+"-"+n2,e.getWeight()<1.0f);
        }
        for (int[] edge:edges_6) {
            Node n1 = new NodeImpl(edge[0]+"");
            Node n2 = new NodeImpl(edge[1]+"");
            Edge e = ((Graph<Node, Edge>) one).findEdge(n1,n2);
            assertNotNull(e);
            assertEquals("Incorrect weight for "+n1+"-"+n2,.666f,e.getWeight(),.001);
        }
        for (int[] edge:edges_3) {
            Node n1 = new NodeImpl(edge[0]+"");
            Node n2 = new NodeImpl(edge[1]+"");
            Edge e = ((Graph<Node, Edge>) one).findEdge(n1,n2);
            assertNotNull(e);
            assertEquals("Incorrect weight for "+n1+"-"+n2,.333f,e.getWeight(),.001);
        }



    }



    public static Test suite() {
        return new TestSuite(JungUtilsTest.class);
    }
}
