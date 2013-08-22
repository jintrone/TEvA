package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.DirectedJungNetwork;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityFrame;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.serialization.CommunityFrameJaxbAdapter;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/8/12
 * Time: 3:11 PM
 */

public class TevaUtils {

    private static Logger logger = Logger.getLogger(TevaUtils.class);

    public static void serialize(Clique clique, File f) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(Clique.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);

//        edu.mit.cci.snatools.topicevolution.Utils.analyzeDrainagePatterns(c);
        FileOutputStream output = new FileOutputStream(f);
        m.marshal(clique, output);
        output.flush();
        output.close();

    }

    public static void serialize(CommunityFrame communityFrame, File f) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(CommunityFrameJaxbAdapter.class, CommunityFrame.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);

//        edu.mit.cci.snatools.topicevolution.Utils.analyzeDrainagePatterns(c);
        FileOutputStream output = new FileOutputStream(f);
        m.marshal(communityFrame, output);
        output.flush();
        output.close();

    }

    public static CommunityModel getCommunityModelFromFile(File file) throws JAXBException, FileNotFoundException {
        return deserialize(file, CommunityModel.class);
    }

    public static List<String> getCommunityRepresentation(Network c, int size) {
        UndirectedJungNetwork graph = new UndirectedJungNetwork();
        List<String> s = new ArrayList<String>();

        for (Edge e : c.getEdges()) {
            graph.add(e);
        }

        final EigenvectorCentrality<Node, Edge> ev = new EigenvectorCentrality<Node, Edge>(graph, new Transformer<Edge, Float>() {
            public Float transform(Edge e) {
                return e.getWeight();
            }
        });
        ev.evaluate();
        List<Node> sortedlist = new ArrayList<Node>(graph.getVertices());
        Collections.sort(sortedlist, new Comparator<Node>() {
            public int compare(Node defaultJungNode, Node defaultJungNode1) {
                double score1 = ev.getVertexScore(defaultJungNode);
                double score2 = ev.getVertexScore(defaultJungNode1);
                if (score1 < score2) return 1;
                else if (score2 == score1) return 0;
                else return -1;

            }
        }
        );

        for (int i = 0; i < Math.min(size, sortedlist.size()); i++) {
            s.add(sortedlist.get(i).getLabel());
        }
        return s;


    }

    /**
     * Creates a graph of all topics / time windows, with each step connected, and each of the link types between topics included
     *
     * @param model
     * @param spawns
     * @param consumes
     * @param informs
     * @return
     */
    public static DirectedJungNetwork createCommunityGraph(CommunityModel model, boolean spawns, boolean consumes, boolean informs) {
        Map<String, List<Node>> nodemap = new HashMap<String, List<Node>>();
        DirectedJungNetwork result = new DirectedJungNetwork();
        for (Community cs : model.getCommunities()) {
            List<Node> nodes = new ArrayList<Node>();
            nodemap.put(cs.getId(), nodes);


            Node last = null;
            for (int i = 0; i < model.getWindows().length; i++) {
                CommunityFrame s = cs.getCommunityAtBin(i);
                if (s == null) {

                    nodes.add(null);

                } else {

                    Node n = new NodeImpl("C" + cs.getId() + "W" + i);
                    n.setProperty("Size", s.getNodes().size());
                    n.setProperty("Window", i);
                    n.setProperty("CommunityId", cs.getId());
                    //n.setProperty("Intensity", (int) (300.0f * (s..getIntegration() * s.getStats().getThreadFocus() * (s.getStats().getNumPosts() / (float) maxposts))));

                    nodes.add(n);
                    result.addVertex(n);
                    if (last != null) {
                        Edge e = new EdgeImpl(last, n, 1.0f, true);
                        result.add(e);
                    }
                    last = n;
                }

            }
            while (nodes.size() > 0 && nodes.get(nodes.size() - 1) == null) {
                nodes.remove(nodes.size() - 1);
            }

        }


        for (int i = 0; i < model.getWindows().length; i++) {

            if (consumes && model.consumers.containsKey(i)) {
                for (CommunityModel.Connection c : model.consumers.get(i)) {
                    List<Node> nodes = nodemap.get(c.source.getId());
                    Node n1 = nodes.get(nodes.size() - 1);
                    Node n2 = nodemap.get(c.target.getId()).get(i);
                    if (n1.getProperty("CommunityId").equals(n2.getProperty("CommunityId"))) {
                        logger.warn("BUG - consumes links should not be to the same community "+c.source.getId()+"->"+c.target.getId()+" in "+i);
                        continue;
                    }
                    Edge e = new EdgeImpl(n1, n2, c.weight, true);
                    e.setProperty("Type", "CONSUMES");
                    result.add(e);

                }
            }
            if (spawns && model.spawners.containsKey(i)) {
                for (CommunityModel.Connection c : model.spawners.get(i)) {
                    Node n1 = nodemap.get(c.source.getId()).get(i - 1);
                    Node n2 = nodemap.get(c.target.getId()).get(i);
                    Edge e = new EdgeImpl(n1, n2, c.weight, true);
                    e.setProperty("Type", "SPAWNS");
                    result.add(e);
                }
            }
            if (informs && model.informs.containsKey(i)) {
                for (CommunityModel.Connection c : model.informs.get(i)) {
                    List<Node> nodes = nodemap.get(c.source.getId());
                    Node n1 = nodes.get(nodes.size() - 1);
                    Node n2 = nodemap.get(c.target.getId()).get(i);
                    Edge e = new EdgeImpl(n1, n2, c.weight, true);
                    e.setProperty("Type", "INFORMS");
                    result.add(e);
                }
            }
        }

        return result;


    }


    public static void addDrainageScoresForCommunityGraph(DirectedJungNetwork graph) {

        for (Node node : graph.getVertices()) {


            node.setProperty("Centrality", getIncidentCommunities(graph, node).size() - 1);


        }

    }

    public static Set<String> getIncidentCommunities(DirectedJungNetwork graph, Node node) {
        Set<String> result = new HashSet<String>();
        //logger.info("In Node: "+node.getProperty("CommunityId")+" Window:"+node.getProperty("Window"));
        for (Edge e : graph.getInEdges(node)) {
            result.addAll(getIncidentCommunities(graph, e.getEndpoints()[0]));
        }
        result.add((String) node.getProperty("CommunityId"));
        return result;
    }


    public static Clique deserializeClique(File f) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(Clique.class);
        Clique x = (Clique) context.createUnmarshaller().unmarshal(new FileInputStream(f));
        return x;
    }

    public static <T> T deserialize(File f, Class... support) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(support);
        Unmarshaller m = context.createUnmarshaller();
        m.setEventHandler(new DefaultValidationEventHandler());
        T x = (T) m.unmarshal(new FileInputStream(f));
        return x;
    }

    public static <T> void serialize(File f, T obj, Class... support) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(support);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);
        FileOutputStream output = new FileOutputStream(f);
        m.marshal(obj, output);
        output.flush();
        output.close();

    }

    /**
     * Creates a graph of all topics / time windows, with each step connected, and each of the link types between topics included
     *
     * @param jaxb
     * @param spawns
     * @param consumes
     * @param informs
     * @return public static UndirectedJungNetwork createCommunityGraph(CommunityModel jaxb, boolean spawns, boolean consumes, boolean informs) {
    Map<String, List<Node>> nodemap = new HashMap<String, List<Node>>();
    UndirectedJungNetwork result = new UndirectedJungNetwork();
    for (Community cs : jaxb.getCommunities()) {
    List<Node> nodes = new ArrayList<Node>();
    nodemap.put(cs.getId(), nodes);
    int maxposts = jaxb.getMaxPostCount();
    for (int i = 0; i < jaxb.getWindows().length; i++) {
    CommunityFrame s = cs.getCommunityAtBin(i);
    if (s == null) continue;
    else {
    NodeImpl n = new NodeImpl("C" + cs.getId());
    n.setProperty("Size", s.getNodes().size());
    n.setProperty("Age", i);
    n.setProperty("CommunityId", cs.getId());
    //TODO add community intensity

    //n.setProperty("Intensity", (int) (300.0f * (s.getStats().getIntegration() * s.getStats().getThreadFocus() * (s.getStats().getNumPosts() / (float) maxposts))));

    nodes.add(n);
    result.addVertex(n);
    }
    }
    }

    for (Community cs : jaxb.getCommunities()) {
    Set<CommunityModel.Connection> destinations = new HashSet<CommunityModel.Connection>();
    if (consumes) {
    for (Map.Entry<Integer,Set<CommunityModel.Connection>> ent:jaxb.consumers.entrySet()) {
    for (CommunityModel.Connection con:ent.getValue()) {
    Node src = null;
    Node dest = null;
    for (Node ns: nodemap.get(cs.getId())) {
    if (ns.getProperty("Age").equals(ent.getKey()-1)) {
    src = ns;
    }   break;
    }
    for (Node ns:nodemap.get(con.target.getId())) {
    if ()
    }
    }
    }
    };
    if (spawns) destinations.addAll(cs.getSpawns());


    for (DestinationJaxb d : destinations) {
    DefaultJungNode src = null;
    DefaultJungNode dest = null;
    for (DefaultJungNode ns : nodemap.get(cs.getCommunityId())) {
    if (ns.getAttribute("Age").equals(d.getWindow() - 1)) {
    src = ns;
    break;
    }
    }

    for (DefaultJungNode nd : nodemap.get(d.getCommunityId())) {
    if (nd.getAttribute("Age").equals(d.getWindow())) {
    dest = nd;
    break;
    }
    }
    result.addEdge(src, dest, 1.0f);
    }

    if (informs) {
    for (DestinationJaxb d : cs.getInforms()) {
    int idx = nodemap.get(cs.getCommunityId()).size() - 1;
    DefaultJungNode src = nodemap.get(cs.getCommunityId()).get(idx);
    DefaultJungNode dest = null;
    for (DefaultJungNode nd : nodemap.get(d.getCommunityId())) {
    if (nd.getAttribute("Age").equals(d.getWindow())) {
    dest = nd;
    break;
    }
    }
    result.addEdge(src, dest, 1.0f);
    }
    }


    }

    for (List<DefaultJungNode> nodes : nodemap.values()) {
    for (int i = 1; i < nodes.size(); i++) {
    result.addEdge(nodes.get(i - 1), nodes.get(i), 1.0f);
    }
    }
    return result;


    }
     */


}
