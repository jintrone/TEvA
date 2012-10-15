package edu.mit.cci.sna;

import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 10:15 PM
 */
public class NetworkUtils {

    /**
     * Ratio of one's edges contained by two
     *
     * @param one
     * @param two
     * @return
     */
    public static float coverage(Network one, Network two) {
        if (one == null || two == null || one.getNodes().isEmpty() || two.getNodes().isEmpty()) {
            return 0f;
        } else {
            Set<Edge> fromedges = new HashSet<Edge>(one.getEdges());
            float denom = fromedges.size();
            fromedges.retainAll(two.getEdges());

            return (float) fromedges.size() / denom;
        }
    }

    public static float similarity(Network one, Network two) {
        if (one == null || two == null || one.getNodes().isEmpty() || two.getNodes().isEmpty()) return 0f;
        Set<Edge> intersection = new HashSet<Edge>(one.getEdges());
        intersection.retainAll(two.getEdges());
        Set<Edge> union = new HashSet<Edge>(one.getEdges());
        union.addAll(two.getEdges());
        return intersection.size() / (float) union.size();
    }

    public static List<Clique> findCliquesContaining(UndirectedJungNetwork graph, Set<Node> nodes, int k) {
        if (nodes.size() >= k) throw new RuntimeException("Clique size must be greater than incoming clique");
        Node[] nary = nodes.toArray(new Node[nodes.size()]);

        for (int i = 0; i < nary.length; i++) {
            for (int j = i + 1; j < nary.length; j++) {
                if (graph.findEdge(nary[i], nary[j]) == null) {
                    throw new RuntimeException("Incoming nodes must form a clique");
                }
            }
        }

        Set<Node> candidates = null;
        for (Node n : nodes) {
            if (candidates == null) {
                candidates = new HashSet<Node>();
                candidates.addAll(graph.getNeighbors(n));
            } else {
                candidates.retainAll(graph.getNeighbors(n));
            }
        }
        candidates.removeAll(nodes);
        return cliquex(graph, new ArrayList<Node>(nodes), new ArrayList<Node>(candidates), k);

    }

    public static List<Clique> cliquex(UndirectedJungNetwork graph, List<Node> included, List<Node> candidates, int cliquesize) {
        List<Clique> result = new ArrayList<Clique>();

        while ((candidates.size() + included.size()) >= cliquesize) {

            // could optimize this
            List<Node> included_copy = new ArrayList<Node>(included);
            List<Node> candidates_copy = new ArrayList<Node>(candidates.subList(1, candidates.size()));


            Node target = candidates.remove(0);
            for (Iterator<Node> i = candidates_copy.iterator(); i.hasNext(); ) {
                if (graph.findEdge(target, i.next()) == null) {
                    i.remove();
                }
            }


            included_copy.add(target);
            if (included_copy.size() == cliquesize) {
                result.add(cliquify(included_copy));
            } else {
                result.addAll(cliquex(graph, included_copy, candidates_copy, cliquesize));
            }


        }
        return result;
    }

    public static Clique cliquify(Collection<Node> nodes) {
        Clique c = new Clique();
        Set<String> nnames = new HashSet<String>();
        for (Node n : nodes) {
            nnames.add(n.getLabel());
        }
        c.setNodes(nnames);
        return c;
    }

    public static void filterEdges(Network currentGraph, float minimumLinkWeight) {
        for (Edge e : new ArrayList<Edge>(currentGraph.getEdges())) {
            if (e.getWeight() < minimumLinkWeight) currentGraph.remove(e);

        }
    }

    public static void createNetworkFile(Network adapter, File f) throws IOException {
        //String filename = prefix + "." + System.currentTimeMillis() + Math.random() + ".net";

        PrintWriter output = new PrintWriter(new FileWriter(f));
        output.println("#" + new Date());
        for (Edge e : adapter.getEdges()) {
            output.println(e.getEndpoints()[0].getLabel() + " " + e.getEndpoints()[1].getLabel() + " " + e.getWeight());
        }
        output.flush();
        output.close();
    }

    public static String simpleString(Network n) {
        StringBuilder builder = new StringBuilder();
        builder.append("Net[");
        for (Node node:n.getNodes()) {
            builder.append(node.getLabel()).append(",");
        }
        builder.append("]");
        return builder.toString();
    }




    public static Network readNetworkFile(File f) throws IOException {
        //String filename = prefix + "." + System.currentTimeMillis() + Math.random() + ".net";
        UndirectedJungNetwork network = new UndirectedJungNetwork();
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) continue;


            String[] tokens = line.split("\\s+");
            network.add(new EdgeImpl(new NodeImpl(tokens[0]), new NodeImpl(tokens[1]), Float.parseFloat(tokens[2]), false));
        }
        return network;
    }
}
