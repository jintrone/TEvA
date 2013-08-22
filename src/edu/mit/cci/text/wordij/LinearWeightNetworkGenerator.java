package edu.mit.cci.text.wordij;


import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Generates a network in which nodes with a degree of indirection of 0 are connected by an
 * edge with a maximum weight of l. Links are created between nodes increasing degree linearly
 * reducing the weight on the edge until degree = max.  The weakest link in the constructed
 * graph will have a weight of 1 - (max-1)/max.
 * <p/>
 * <p/>
 * User: jintrone
 * Date: 5/23/11
 * Time: 11:42 AM
 */
public class LinearWeightNetworkGenerator implements TextToNetworkGenerator {


    int maxIndirection = 2;
    int ngramsize = 2;


    Logger log = Logger.getLogger(LinearWeightNetworkGenerator.class);

    public LinearWeightNetworkGenerator(int maxindirection, int ngramsize) {
        this.maxIndirection = maxindirection;
        this.ngramsize = ngramsize;

    }


    public Network calculateWeights(List<String> preceding, List<String> sample) {

        if (sample == null || sample.size() == 0) return null;
        log.info("Calculating network on sample (" + sample.size() + " tokens)");
        UndirectedJungNetwork result = new UndirectedJungNetwork();
        Map<String, Node> vertices = new HashMap<String, Node>();
        List<Node> prebuffer = new ArrayList<Node>();
        if (preceding != null && !preceding.isEmpty()) {
            for (String word : preceding.subList(Math.max(0, preceding.size() - ngramsize + 1), preceding.size())) {
                Node vertex = vertices.get(word);
                if (vertex == null) {
                    vertex = new NodeImpl(word);
                    vertices.put(word, vertex);
                }
                prebuffer.add(vertex);
            }

        }
        List<Node> buffer = new ArrayList<Node>();


        for (String word : sample) {
            Node vertex = vertices.get(word);
            if (vertex == null) {
                vertex = new NodeImpl(word);
                vertices.put(word, vertex);
            }
            buffer.add(vertex);
            if (!prebuffer.isEmpty()) {
                for (Node from:prebuffer) {
                    Edge edge = result.findEdge(from, vertex);
                     if (edge == null) {
                         edge = new EdgeImpl(from, vertex, 1.0f, false);
                         result.addEdge(edge, from, vertex);
                         log.debug("Adding prebuffered edge: " + edge);

                     }
                }
                prebuffer.remove(0);
            }


            if (buffer.size() < ngramsize) {
                continue;
            } else if (buffer.size() > ngramsize) {
                buffer.remove(0);
            }

            for (int i = buffer.size() - 1; i >= 1; i--) {
                for (int j = buffer.size() - 2; j >= 0; j--) {
                    Node from = buffer.get(i);
                    Node to = buffer.get(j);
                    Edge edge = result.findEdge(from, to);
                    if (edge != null) {
                        //this shouldn't really happen.
                        if (edge.getWeight() < 1.0f) {
                            edge.setWeight(1.0f);
                        }
                    } else {
                        edge = new EdgeImpl(from, to, 1.0f, false);
                        result.addEdge(edge, from, to);
                        log.debug("Adding edge: " + edge);

                    }
                }
            }
        }

        if (maxIndirection > 1) updateIndirection(result);
        return result;
    }


    private void updateIndirection(UndirectedJungNetwork graph) {
        float delta = 1.0f / (float) maxIndirection;
        UnweightedShortestPath<Node, Edge> paths = new UnweightedShortestPath<Node, Edge>(graph);
        List<Edge> edges = new ArrayList<Edge>();
        for (Node node : graph.getVertices()) {
            Map<Node, Number> p = paths.getDistanceMap(node);
            for (Map.Entry<Node, Number> ent : p.entrySet()) {
                if (ent.getValue().floatValue() > 1.0f && ent.getValue().floatValue() <= maxIndirection) {
                    Edge nedge = new EdgeImpl(node, ent.getKey(), 1f - (ent.getValue().intValue() - 1) * delta, false);
                    edges.add(nedge);
                }
            }
        }

        for (Edge edge : edges) {
            Edge existing = graph.findEdge(edge.getEndpoints()[0], edge.getEndpoints()[1]);
            if (existing == null) {
                graph.addEdge(edge, edge.getEndpoints()[0], edge.getEndpoints()[1]);
            } else {
                if (edge.getWeight() > existing.getWeight()) {
                    existing.setWeight(edge.getWeight());
                }
            }
        }


    }


}
