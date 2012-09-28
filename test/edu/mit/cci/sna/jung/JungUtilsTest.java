package edu.mit.cci.sna.jung;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

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
    public void testCopy() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: merge(Graph<Node, Edge> to, Graph<Node, Edge> from, MergePolicy policy)
     *
     */
    public void testMergeForToFromPolicy() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: merge(Graph<Node, Edge> to, Graph<Node, Edge> from)
     *
     */
    public void testMergeForToFrom() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: writeGenericFile(Writer out, AbstractGraph<NodeImpl,EdgeImpl> graph)
     *
     */
    public void testWriteGenericFile() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: merge(Edge one, Edge two)
     *
     */
    public void testMergeForOneTwo() throws Exception {
        //TODO: Test goes here...
    }



    public static Test suite() {
        return new TestSuite(JungUtilsTest.class);
    }
}
