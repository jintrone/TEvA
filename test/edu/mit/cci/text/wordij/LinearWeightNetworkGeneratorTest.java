package edu.mit.cci.text.wordij;

import com.sun.tools.corba.se.idl.StringGen;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * LinearWeightCalculator Tester.
 *
 * @author <Authors name>
 * @since <pre>09/27/2012</pre>
 * @version 1.0
 */
public class LinearWeightNetworkGeneratorTest extends TestCase {
    public LinearWeightNetworkGeneratorTest(String name) {
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
     * Method: calculateWeights(List<String> sample)
     *
     */
    public void testCalculateWeights() throws Exception {
        String[] input = new String[]{"1","2","3","4","5","3","6","2"};
        LinearWeightNetworkGenerator generator = new LinearWeightNetworkGenerator(3,2);
        UndirectedJungNetwork n = (UndirectedJungNetwork)generator.calculateWeights(Arrays.asList(input));
        Collection<Edge> e = n.getEdges();
        //edges weight = 1
        String[][] edges_1 = new String[][] {
                {"1","2"},
                {"2","3"},
                {"2","6"},
                {"3","4"},
                {"3","5"},
                {"3","6"},
                {"4","5"}};

        String[][] edges_2 = new String[][] {
                {"1","3"},
                {"2","4"},
                {"2","5"},
                {"5","6"},
                {"4","6"},
                {"1","6"}
        };

        String[][] edges_3 = new String[][]{
                {"1","4"},
                {"1","5"}

        };

        assertEquals("Incorrect number of edges",edges_1.length+edges_2.length+edges_3.length,e.size());
        Map<String,Node> nodemap = new HashMap<String, Node>();
        for (Node node:n.getVertices()) {
            nodemap.put(node.getId(),node);
        }
        for (String[] te:edges_1) {
            Node n1 = nodemap.get(te[0]);
            Node n2 = nodemap.get(te[1]);
            assertNotNull(n1);
            assertNotNull(n2);
            Edge edge = n.findEdge(n1,n2);
            assertNotNull(edge);
            assertEquals(edge.toString(),1.0f, edge.getWeight(),.01);
        }

         for (String[] te:edges_2) {
            Node n1 = nodemap.get(te[0]);
            Node n2 = nodemap.get(te[1]);
            assertNotNull(n1);
            assertNotNull(n2);
            Edge edge = n.findEdge(n1,n2);
            assertNotNull(edge);
            assertEquals(edge.toString(),.66f, edge.getWeight(),.01);
        }

        for (String[] te:edges_3) {
            Node n1 = nodemap.get(te[0]);
            Node n2 = nodemap.get(te[1]);
            assertNotNull(n1);
            assertNotNull(n2);
            Edge edge = n.findEdge(n1,n2);
            assertNotNull(edge);
            assertEquals(edge.toString(),.33f, edge.getWeight(),.01);
        }


    }






    public static Test suite() {
        return new TestSuite(LinearWeightNetworkGeneratorTest.class);
    }
}
