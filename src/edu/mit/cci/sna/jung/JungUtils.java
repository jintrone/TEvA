package edu.mit.cci.sna.jung;


import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.dynamic.Spell;
import it.uniroma1.dis.wsngroup.gexf4j.core.dynamic.TimeFormat;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.SpellImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;



import java.io.*;
import java.util.*;

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


    public static void createDynamicGraph(File out, String description, List<Network> data, boolean directed, List<AttributeHolder> atts) throws IOException {
        LinkedHashMap<Date, Network> result = new LinkedHashMap<Date, Network>();
        long start = System.currentTimeMillis();
        int i = 0;
        for (Network a : data) {
            result.put(new Date(start + (1000l * 60l * 60l * 24l * i)), a);
            i++;

        }
        result.put(new Date(start + (1000l * 60l * 60l * 24l * i)), null);
        createDynamicGraph(out, description, result, directed, atts);
    }

    public static void createDynamicGraph(File out, String description, List<Network> data, boolean directed) throws IOException {
        createDynamicGraph(out, description, data, directed, null);
    }

    public static class AttributeHolder {
        public String[] attnames;
        public Map<String, String[]> values = new HashMap<String, String[]>();

        public AttributeHolder(String[] attnames) {
            this.attnames = attnames;

        }

        public void setAttributeValues(String node, String[] vals) {
            values.put(node, vals);
        }
    }


    public static void createDynamicGraph(File out, String description, LinkedHashMap<Date, Network> data, boolean directed, List<AttributeHolder> atts) throws IOException {
        Gexf gexf = new GexfImpl();
        gexf.getMetadata().setLastModified(new Date()).setCreator("Josh Introne").setDescription(description);
        gexf.getGraph().setMode(Mode.DYNAMIC).setTimeType(TimeFormat.XSDDATETIME);
        gexf.getGraph().setDefaultEdgeType(directed ? EdgeType.DIRECTED : EdgeType.UNDIRECTED);
        AttributeList aList = new AttributeListImpl(AttributeClass.EDGE);
        aList.setMode(Mode.DYNAMIC);
        Attribute weight = aList.createAttribute("weight", AttributeType.FLOAT, "Weight");

        AttributeList nList = new AttributeListImpl(AttributeClass.NODE);
        nList.setMode(Mode.DYNAMIC);

        gexf.getGraph().getAttributeLists().add(aList);
        gexf.getGraph().getAttributeLists().add(nList);
        List<Attribute> nAtts = new ArrayList<Attribute>();
        if (atts != null) {
            AttributeHolder h = atts.get(0);
            for (String s : h.attnames) {
                nAtts.add(nList.createAttribute(AttributeType.STRING, s));
            }
        }

        Map<Collection<String>, it.uniroma1.dis.wsngroup.gexf4j.core.Edge> edges = new HashMap<>();
        Map<String, it.uniroma1.dis.wsngroup.gexf4j.core.Node> nodes = new HashMap<>();
        Map<String, List<Integer>> lifetime = new HashMap<String, List<Integer>>();
        List<Network> networks = new ArrayList<>(data.values());
        List<Date> dates = new ArrayList<>(data.keySet());
        for (int i = 0; i < networks.size() - 1; i++) {
            AttributeHolder a = atts != null ? atts.get(i) : null;
            Set<String> processed = new HashSet<>();
            for (edu.mit.cci.sna.Edge e : networks.get(i).getEdges()) {
                Node[] ends = e.getEndpoints();


                it.uniroma1.dis.wsngroup.gexf4j.core.Node gfrom = nodes.get(ends[0].getId());
                List<Integer> active = lifetime.get(ends[0].getId());
                if (active == null) {
                    lifetime.put(ends[0].getId(), active = new ArrayList<>());
                }
                if (!active.contains(i)) active.add(i);

                if (gfrom == null) {
                    if (ends[0].getId().trim().isEmpty()) {
                        System.err.println("What is up?");
                    }
                    gfrom = gexf.getGraph().createNode(ends[0].getId());
                    gfrom.setLabel(gfrom.getId());
                    nodes.put(ends[0].getId(), gfrom);
                }
                if (a != null && !processed.contains(gfrom.getLabel()) && a.values.containsKey(gfrom.getLabel())) {
                    processed.add(gfrom.getLabel());
                    for (int ai = 0; ai < nAtts.size(); ai++) {
                        gfrom.getAttributeValues().createValue(nAtts.get(ai), a.values.get(gfrom.getLabel())[ai]).setStartValue(dates.get(i)).setEndValue(dates.get(i + 1));
                    }
                }


                it.uniroma1.dis.wsngroup.gexf4j.core.Node gto = nodes.get(ends[1].getId());
                active = lifetime.get(ends[1]);
                if (active == null) {
                    lifetime.put(ends[1].getId(), active = new ArrayList<Integer>());
                }
                if (!active.contains(i)) active.add(i);

                if (gto == null) {
                    gto = gexf.getGraph().createNode(ends[1].getId());
                    gto.setLabel(gto.getId());
                    nodes.put(ends[1].getId(), gto);
                }

                if (a != null && !processed.contains(gto.getLabel()) && a.values.containsKey(gto.getLabel())) {
                    processed.add(gto.getLabel());
                    for (int ai = 0; ai < nAtts.size(); ai++) {
                        gto.getAttributeValues().createValue(nAtts.get(ai), a.values.get(gto.getLabel())[ai]).setStartValue(dates.get(i)).setEndValue(dates.get(i + 1));
                    }
                }
                Collection cE = Arrays.asList(ends[0].getId(),ends[1].getId());
                if (!directed) {
                   cE = new HashSet(cE);
                }

                it.uniroma1.dis.wsngroup.gexf4j.core.Edge ge = edges.get(cE);
                if (ge == null) {
                    ge = gfrom.connectTo(gto);
                    ge.setEdgeType(directed ? EdgeType.DIRECTED : EdgeType.UNDIRECTED);
                    edges.put(cE, ge);

                }

                ge.getAttributeValues().createValue(weight, e.getWeight() + "").setStartValue(dates.get(i)).setEndValue(dates.get(i + 1));


            }

        }


        for (String s : nodes.keySet()) {
            it.uniroma1.dis.wsngroup.gexf4j.core.Node gnode = nodes.get(s);

            Spell current = null;
            int last = -1;
            if (lifetime.get(s) == null) continue;

            Collections.sort(lifetime.get(s));
            for (Integer i : lifetime.get(s)) {
                if (current == null) {
                    gnode.getSpells().add(current = new SpellImpl());
                    current.setStartValue(dates.get(i));
                    last = i;
                } else if (i - last > 1) {
                    current.setEndValue(dates.get(last + 1));
                    gnode.getSpells().add(current = new SpellImpl());
                    current.setStartValue(dates.get(i));
                    last = i;
                } else {
                    last = i;
                }

            }
            if (current != null && !current.hasEndDate()) {
                current.setEndValue(dates.get(last + 1));
            }


        }

        new StaxGraphWriter().writeToStream(gexf, new FileOutputStream(out),"UTF8");


    }


}
