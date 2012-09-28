package edu.mit.cci.sna.jung;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

import java.util.Collection;

/**
 * User: jintrone
 * Date: 9/21/12
 * Time: 8:50 PM
 */
public class UndirectedJungNetwork extends UndirectedSparseMultigraph<Node,Edge> implements Network {

    public UndirectedJungNetwork() {}


    public UndirectedJungNetwork(DirectedJungNetwork copy) {
        for (Edge edge:copy.getEdges()){
            Node[] nodes = edge.getEndpoints();
            this.addEdge(new EdgeImpl(nodes[0],nodes[1],edge.getWeight(),true),copy.getEndpoints(edge));
        }
    }

    public Collection<Node> getNodes() {
        return getVertices();
    }

    public boolean isDirected() {
        return false;
    }
}
