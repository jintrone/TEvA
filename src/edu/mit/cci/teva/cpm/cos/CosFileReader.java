package edu.mit.cci.teva.cpm.cos;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.CliqueDecoratedNetwork;
import edu.mit.cci.sna.CliqueDecorater;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.util.F;
import edu.mit.cci.util.U;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

/**
 * User: jintrone
 * Date: 11/13/12
 * Time: 4:17 PM
 */
public class CosFileReader {

    private static Logger logger = Logger.getLogger(CosFileReader.class);

    public static List<CliqueDecoratedNetwork> readCommunities(String networkname, int cliquesize, File basedir) throws CommunityFinderException {
        File communities = new File(basedir, cliquesize + "_communities.txt");
        File mapping_file = new File(basedir, networkname + ".map");
        File max_cliques = new File(basedir, networkname + ".mcliques");
        File networkfile = new File(basedir, networkname);

        logger.info("Communities file: " + communities.getAbsolutePath());
        logger.info("Mapping file: " + mapping_file.getAbsolutePath());
        logger.info("Max cliques file: " + max_cliques.getAbsolutePath());
        logger.info("Network file: " + networkfile.getAbsolutePath());


        if (!communities.exists() || !mapping_file.exists() || !max_cliques.exists()) {
            throw new CommunityFinderException("Missing required files");
        }

        logger.debug("Begin read map");
        Map<Integer, Node> idNodemap = readMap(mapping_file);
        logger.debug("Done");
        final Map<String, Node> namedNodeMap = new HashMap<String, Node>();
        for (Node n : idNodemap.values()) {
            namedNodeMap.put(n.getLabel(), n);
        }

        // UndirectedJungNetwork network = readNetwork(networkfile, namedNodeMap);

        final Map<String, Set<Clique>> cliques = new HashMap<String, Set<Clique>>();

        logger.debug("Begin read communities");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(communities));
            BufferedReader reader = new BufferedReader(isr, 32768);

            int i = 0;
            StringBuilder buffer = new StringBuilder(16);
            String head = null;
            BitSet b = new BitSet(cliques.size());
            int line = 0;
            while ((i = reader.read()) > -1) {
                switch ((char) i) {

                    case ':':
                        head = buffer.toString();
                        buffer = new StringBuilder(16);
                        if (!cliques.containsKey(head)) {
                            cliques.put(head,new HashSet<Clique>());
                        }
                        break;

                    case ' ':
                        b.set(Integer.parseInt(buffer.toString()));
                        buffer = new StringBuilder(16);
                        break;

                    case '\n':
                        if (buffer.length() > 0) {
                            b.set(Integer.parseInt(buffer.toString()));
                            buffer = new StringBuilder(16);
                        }
                        Clique c = new Clique();
                        addNodes(c,b,idNodemap);
                        cliques.get(head).add(c);
                        if (line++ % 1000 == 0) {
                            logger.debug("Line " + line);
                        }
                        break;

                    default:
                        buffer.append((char) i);
                }

            }

        } catch (IOException e) {
            throw new CommunityFinderException("Error reading mapping file");
        }
        logger.debug("Done");


        //to assist with interpretation:
        //loop through all set of cliques
        //add edges from each set to a network
        //return list of networks
        final List<CliqueDecoratedNetwork> result = new ArrayList<CliqueDecoratedNetwork>();
        U.map(cliques.entrySet()).forEach(new F<Map.Entry<String, Set<Clique>>>() {
            public void apply(Map.Entry<String, Set<Clique>> stringSetEntry) {
                final UndirectedJungNetwork net = new UndirectedJungNetwork();
                U.map(stringSetEntry.getValue()).forEach(new F<Clique>() {
                    public void apply(Clique clique) {
                        U.map(clique.getEdgeSet()).forEach(new F<Set<String>>() {
                            public void apply(Set<String> strings) {
                                String[] toArray = strings.toArray(new String[2]);
                                net.add(new EdgeImpl(namedNodeMap.get(toArray[0]), namedNodeMap.get(toArray[1])));
                            }
                        });
                    }
                });
                CliqueDecorater d = new CliqueDecorater(net);
                d.addCliques(cliques.get(stringSetEntry.getKey()));
                result.add(d);
            }
        });

//        for (final String s : nodes.keySet()) {
//            UndirectedJungNetwork community = (UndirectedJungNetwork) new VertexPredicateFilter<Node, Edge>(new Predicate<Node>() {
//                public boolean evaluate(Node node) {
//                    return nodes.get(s).contains(node);
//                }
//            }).transform(network);
//            CliqueDecorater d = new CliqueDecorater(community);
//            d.addCliques(cliques.get(s));
//            result.add(d);
//        }
        return result;


    }

    private static void addNodes(Clique c, BitSet set, Map<Integer,Node> idmap) {
      for (int i=0;i<set.length();i++) {
          if (set.get(i)) {
              c.addNode(idmap.get(i).getLabel());
              set.clear(i);
          }
      }
    }

    private static UndirectedJungNetwork readNetwork(File networkfile, Map<String, Node> namedNodeMap) throws CommunityFinderException {
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


    /**
     * Create a map of ID->Node(String label)
     *
     * @param mapping_file
     * @return
     * @throws CommunityFinderException
     */
    public static Map<Integer, Node> readMap(File mapping_file) throws CommunityFinderException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mapping_file));
            Map<Integer, Node> result = new HashMap<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens.length < 2) {
                    throw new CommunityFinderException("Malformed node map file");
                }
                result.put((Integer.parseInt(tokens[1])), new NodeImpl(tokens[0]));


            }
            return result;
        } catch (IOException e) {
            throw new CommunityFinderException("Error reading node map", e);
        }

    }


}

