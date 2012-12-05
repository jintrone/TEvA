package edu.mit.cci.teva.cpm.cos;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.CliqueDecoratedNetwork;
import edu.mit.cci.sna.CliqueDecorater;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import org.apache.commons.collections15.Predicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 11/13/12
 * Time: 4:17 PM
 */
public class CosFileReader {

    public static List<CliqueDecoratedNetwork> readCommunities(String networkname, int cliquesize, File basedir) throws CommunityFinderException {
        File communities = new File(basedir, cliquesize + "_communities.txt");
        File mapping_file = new File(basedir, networkname + ".map");
        File max_cliques = new File(basedir, networkname + ".mcliques");
        File networkfile = new File(basedir, networkname);

        if (!communities.exists() || !mapping_file.exists() || !max_cliques.exists()) {
            throw new CommunityFinderException("Missing required files");
        }


        Map<String, Node> idNodemap = readMap(mapping_file);
        Map<String, Node> namedNodeMap = new HashMap<String, Node>();
        for (Node n : idNodemap.values()) {
            namedNodeMap.put(n.getLabel(), n);
        }

        UndirectedJungNetwork network = readNetwork(networkfile, namedNodeMap);
        final Map<String, Set<Node>> nodes = new HashMap<String, Set<Node>>();
        Map<String, Set<Clique>> cliques = new HashMap<String, Set<Clique>>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(communities));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                String[] firsttoken = tokens[0].split(":");
                if (!nodes.containsKey(firsttoken[0])) {
                    nodes.put(firsttoken[0], new HashSet<Node>());
                    cliques.put(firsttoken[0],new HashSet<Clique>());
                }
                Set<Node> nodeset = new HashSet<Node>();
                nodeset.add(idNodemap.get(firsttoken[1]));
                for (int j =1;j<tokens.length;j++) {
                    nodeset.add(idNodemap.get(tokens[j]));
                }
                Clique c = new Clique(Integer.parseInt(firsttoken[0]));
                c.setUsingNodes(nodeset);
                nodes.get(firsttoken[0]).addAll(nodeset);

            }

        } catch (IOException e) {
            throw new CommunityFinderException("Error reading mapping file");
        }

        List<CliqueDecoratedNetwork> result = new ArrayList<CliqueDecoratedNetwork>();
        for (final String s:nodes.keySet()) {
           UndirectedJungNetwork community = (UndirectedJungNetwork) new VertexPredicateFilter<Node,Edge>(new Predicate<Node>() {
               public boolean evaluate(Node node) {
                   return nodes.get(s).contains(node);
               }
           }).transform(network);
            CliqueDecorater d = new CliqueDecorater(community);
            d.addCliques(cliques.get(s));
        }
        return result;



    }

    private static UndirectedJungNetwork readNetwork(File networkfile, Map<String, Node> namedNodeMap) throws  CommunityFinderException {
        try {
        BufferedReader reader = new BufferedReader(new FileReader(networkfile));
        UndirectedJungNetwork network = new UndirectedJungNetwork();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens.length < 2) {
                throw new CommunityFinderException("Malformed network file");
            }
            network.addEdge(namedNodeMap.get(tokens[0]), namedNodeMap.get(tokens[1]), 1.0f);

        }
        return network;
        } catch (IOException e) {
           throw new CommunityFinderException("Error reading network", e);
        }
    }


    public static Map<String, Node> readMap(File mapping_file) throws CommunityFinderException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mapping_file));
            Map<String, Node> result = new HashMap<String, Node>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens.length < 2) {
                    throw new CommunityFinderException("Malformed node map file");
                }
                result.put((tokens[1]), new NodeImpl(tokens[0]));


            }
            return result;
        } catch (IOException e) {
            throw new CommunityFinderException("Error reading node map", e);
        }

    }


}

