package edu.mit.cci.sna.jung;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;

import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 5/23/11
 * Time: 11:29 AM
 */
public class JungUtils {

//    public static DefaultUndirectedJungGraph makeUndirectedGraph(DefaultJungGraph g) {
//        DefaultUndirectedJungGraph graph =
//    }

    public static enum MergePolicy {
        ADD {

            public float merge(Edge one, Edge two) {
                return one.getWeight()+two.getWeight();

            }
        },
        MAX {

            public float merge(Edge one, Edge two) {
                return Math.max(one.getWeight(),two.getWeight());
            }
        };

        public abstract float merge(Edge one, Edge two);
    }

    public static AbstractGraph<Node,Edge> copy(AbstractGraph<NodeImpl,EdgeImpl> graph, boolean directed) {
       AbstractGraph<Node,Edge> result = directed?new DirectedJungNetwork():new UndirectedJungNetwork();
        for (NodeImpl node:graph.getVertices()) {
            result.addVertex(node);
        }
        for (EdgeImpl edge:graph.getEdges()){
            Pair<NodeImpl> pair = graph.getEndpoints(edge);
            result.addEdge(edge,pair.getFirst(),pair.getSecond());
        }
        return result;
    }




    public static void merge(Graph<Node, Edge> to, Graph<Node, Edge> from,MergePolicy policy) {
        if (from == null || from.getVertexCount()==0) return;
        Map<String, Node> labelmap = new HashMap<String, Node>();
        for (Node node : to.getVertices()) {
            labelmap.put(node.getLabel(), node);
        }

        for (Node node : from.getVertices()) {
            if (!labelmap.containsKey(node.getLabel())) {
                to.addVertex(node);
                labelmap.put(node.getLabel(), node);
            }
        }

        for (Edge edge : from.getEdges()) {
            //double check if this is using equality or what
            Pair<Node> p = from.getEndpoints(edge);
            Node src = labelmap.get(p.getFirst().getLabel());
            Node dest = labelmap.get(p.getSecond().getLabel());
            Edge e = to.findEdge(src, dest);
            if (e != null) {
                e.setWeight(policy.merge(e,edge));
            } else {
                EdgeImpl nedge = new EdgeImpl();
                nedge.setWeight(edge.getWeight());
                to.addEdge(nedge, src, dest);
            }
        }
    }

    public static void merge(Graph<Node, Edge> to, Graph<Node, Edge> from) {
        merge(to,from, MergePolicy.ADD);
    }



    public static void writeGenericFile(Writer out, AbstractGraph<NodeImpl,EdgeImpl> graph) throws IOException {
        for (EdgeImpl e:graph.getEdges()) {
            Pair<NodeImpl> pair  = graph.getEndpoints(e);
            out.write(pair.getFirst().getLabel() + " " + pair.getSecond().getLabel() + " " + e.getWeight()+"\n");
        }
        out.flush();
    }


}
