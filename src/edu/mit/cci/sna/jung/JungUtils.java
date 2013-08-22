package edu.mit.cci.sna.jung;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
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

    private static Logger log = Logger.getLogger(JungUtils.class);

    public static enum MergePolicy {
        ADD {
            public float merge(Edge one, Edge two) {
                return one.getWeight() + two.getWeight();

            }
        },

        ADD_MAX_1 {
            public float merge(Edge one, Edge two) {
                return Math.min(1.0f, one.getWeight() + two.getWeight());

            }
        },
        MAX {
            public float merge(Edge one, Edge two) {
                return Math.max(one.getWeight(), two.getWeight());
            }
        };

        public abstract float merge(Edge one, Edge two);
    }

    public static void normalizeEdgeWeights(Network net) {
        float max = 0.0f;
        for (Edge e:net.getEdges()) {
            max = Math.max(max,e.getWeight());
        }
        for (Edge e:net.getEdges()) {
            e.setWeight(e.getWeight()/max);
        }
    }


    public static AbstractGraph<Node, Edge> copy(AbstractGraph<NodeImpl, EdgeImpl> graph, boolean directed) {
        AbstractGraph<Node, Edge> result = directed ? new DirectedJungNetwork() : new UndirectedJungNetwork();
        for (NodeImpl node : graph.getVertices()) {
            result.addVertex(node);
        }
        for (EdgeImpl edge : graph.getEdges()) {
            Pair<NodeImpl> pair = graph.getEndpoints(edge);
            result.addEdge(edge, pair.getFirst(), pair.getSecond());
        }
        return result;
    }


    public static void merge(Graph<Node, Edge> to, Graph<Node, Edge> from, MergePolicy policy) {
        if (from == null || from.getVertexCount() == 0) return;
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
                e.setWeight(policy.merge(e, edge));
            } else {
                EdgeImpl nedge = new EdgeImpl(src, dest);
                nedge.setWeight(edge.getWeight());
                to.addEdge(nedge, src, dest);
            }
        }
    }

    public static void merge(Graph<Node, Edge> to, Graph<Node, Edge> from) {
        merge(to, from, MergePolicy.ADD);
    }


    public static void writeGenericFile(Writer out, AbstractGraph<NodeImpl, EdgeImpl> graph) throws IOException {
        for (EdgeImpl e : graph.getEdges()) {
            Pair<NodeImpl> pair = graph.getEndpoints(e);
            out.write(pair.getFirst().getLabel() + " " + pair.getSecond().getLabel() + " " + e.getWeight() + "\n");
        }
        out.flush();
    }

    public static void writeGraphML(DirectedJungNetwork graph, Map<String, Object> nodeatts, String filename) throws IOException {
        writeGraphML(graph,nodeatts, Collections.<String, Object>emptyMap(),filename);
    }

    public static void writeGraphML(DirectedJungNetwork graph, Map<String, Object> nodeatts,Map<String,Object> edgeatts, String filename) throws IOException {
        MyGraphMLWriter<Node, Edge> writer = new MyGraphMLWriter<Node, Edge>();
        for (Map.Entry<String, Object> ent : nodeatts.entrySet()) {
            final String key = ent.getKey();
            final String[] clazz = new String[]{"string"};
            if (ent.getValue() instanceof Integer) {
                clazz[0] = "int";
            } else if (ent.getValue() instanceof Float || ent.getValue() instanceof Double) {
                clazz[0] = "float";
            }


            writer.addVertexData(ent.getKey(), "", clazz[0], ent.getValue().toString(), new Transformer<Node, String>() {

                public String transform(Node defaultJungNode) {
                    Object o = defaultJungNode.getProperty(key);
                    if (o == null) {
                        log.warn("Property [" + key + "] for node " + defaultJungNode + " is null");
                        log.warn("Other props: " + defaultJungNode.getProperties());
                    }
                    return defaultJungNode.getProperty(key).toString();
                }
            });
        }
        for (Map.Entry<String, Object> ent : edgeatts.entrySet()) {
            final String key = ent.getKey();
            final String[] clazz = new String[]{"string"};
            if (ent.getValue() instanceof Integer) {
                clazz[0] = "int";
            } else if (ent.getValue() instanceof Float || ent.getValue() instanceof Double) {
                clazz[0] = "float";
            }


            writer.addEdgeData(ent.getKey(), "", clazz[0], ent.getValue().toString(), new Transformer<Edge, String>() {

                public String transform(Edge defaultJungEdge) {
                    Object o = defaultJungEdge.getProperty(key);
                    if (o == null) {
                        log.warn("Property [" + key + "] for edge " + defaultJungEdge + " is null");
                        log.warn("Other props: " + defaultJungEdge.getProperties());
                        return "";
                    }
                    return o.toString();
                }
            });
        }

        FileWriter out = new FileWriter(filename);
        writer.save(graph, out);

    }


}
