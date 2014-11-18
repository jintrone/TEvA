package edu.mit.cci.sna.jung;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;

/**
 * User: jintrone
 * Date: 9/21/12
 * Time: 8:50 PM
 */
public class UndirectedJungNetwork extends UndirectedSparseMultigraph<Node, Edge> implements Network {

    public UndirectedJungNetwork() {
    }

    Logger log = Logger.getLogger(UndirectedJungNetwork.class);

    public UndirectedJungNetwork(DirectedJungNetwork copy) {
        for (Edge edge : copy.getEdges()) {
            Node[] nodes = edge.getEndpoints();
            this.addEdge(new EdgeImpl(nodes[0], nodes[1], edge.getWeight(), true), copy.getEndpoints(edge));
        }
    }

    public Collection<Node> getNodes() {
        return getVertices();
    }


    public boolean isDirected() {
        return false;
    }

    public void remove(Edge e) {
        this.removeEdge(e);
    }

    public void remove(Node n) {
        this.removeVertex(n);
    }

    public void add(Node n) {
        this.addVertex(n);
    }

    public void add(Edge e) {
        if (!containsEdge(e)) {
            this.addEdge(e, e.getEndpoints()[0], e.getEndpoints()[1]);
        }
    }

    public Collection<Edge> getEdges() {
        return Collections.unmodifiableSet(edges.keySet());
    }

    public Collection<Node> getVertices() {
        return Collections.unmodifiableSet(vertices.keySet());
    }

    public Edge addEdge(Node node1, Node node2, float weight) {
        Edge e = findEdge(node1, node2);
        if (e != null) {
            log.debug("Already added edge, updating weight");
        } else {
            e = new EdgeImpl(node1, node2, weight, false);
            this.addEdge(e, node1, node2);
        }
        return e;
    }

    public Edge addAndSum(Node node1, Node node2, float weight) {
        Edge e = findEdge(node1, node2);
        if (e != null) {
            e.setWeight(e.getWeight()+weight);
        } else {
            e = new EdgeImpl(node1, node2, weight, false);
            this.addEdge(e, node1, node2);
        }
        return e;
    }


}
