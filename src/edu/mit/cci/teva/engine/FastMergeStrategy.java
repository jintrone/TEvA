package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.cpm.cfinder.CFinderCommunityFinder;
import edu.mit.cci.util.U;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 6/20/12
 * Time: 6:55 AM
 */
public class FastMergeStrategy implements MergeStrategy {

    private static Logger log = Logger.getLogger(FastMergeStrategy.class);

    int k = -1;

    private Map<Clique, Node> cliqueAdjacencyNodeMap = new HashMap<Clique, Node>();


    public FastMergeStrategy(int k) {

        this.k = k;

    }


    /**
     * Walks through the maximal cliques that make up the communities in either frame,
     * and merges them where appropriate, or extends them if abutting edges that are not already
     * in cliques should be added.
     *
     *
     * @param from
     * @param cdfrom
     * @param to
     * @param cdto
     * @param window
     * @return
     */
    public List<Network> process(Network from, Collection<CommunityFrame> cdfrom, Network to, Collection<CommunityFrame> cdto, int window)  {
        cliqueAdjacencyNodeMap.clear();


        UndirectedJungNetwork merged = new UndirectedJungNetwork();


        Set<Network> cdFrom = new HashSet<Network>(cdfrom);
        Set<Network> cdTo = new HashSet<Network>(cdto);

        Set<Node> nodeset = new HashSet<Node>();
        Map<Set<Node>, Edge> edgemap = new HashMap<Set<Node>, Edge>();
        Set<Set<Node>> newEdges = new HashSet<Set<Node>>();

        log.debug("Start state");
        log.debug("From network: "+from.getNodes().size()+" nodes, "+from.getEdges().size()+" edges, "+cdfrom.size()+"communities");
        log.debug("To network: "+to.getNodes().size()+" nodes, "+to.getEdges().size()+" edges, "+cdto.size()+"communities");

        //create new graph, separating shared edges from unshared edges during the merge process
        int fedges = from.getEdges().size();

        //first loop just merges the networks, and builds up a bunch of bookkeeping
        for (Edge e : U.multiIterator(from.getEdges(), to.getEdges())) {
            boolean second = fedges-- < 0;


            Set<Node> pair = new HashSet<Node>();
            pair.add(e.getEndpoints()[0]);
            pair.add(e.getEndpoints()[1]);


            //segregate edges
            if (!second) {
                newEdges.add(pair);
            } else {
                if (!newEdges.remove(pair)) {
                    newEdges.add(pair);
                }
            }


            Edge existing = edgemap.get(pair);
            if (existing == null) {
                Node node1 = e.getEndpoints()[0], node2 = e.getEndpoints()[1];
                if (!nodeset.contains(node1)) {
                    merged.addVertex(node1);
                    nodeset.add(node1);
                }

                if (!nodeset.contains(node2)) {
                    merged.addVertex(node2);
                    nodeset.add(node2);

                }

                existing = merged.addEdge(node1, node2, e.getWeight());

            } else {

                //update weight if necessary
                if (e.getWeight() > existing.getWeight()) {
                    existing.setWeight(e.getWeight());
                }
            }

            //build map of existing edges in merged
            edgemap.put(pair, existing);
        }

        log.debug("Newedges: "+newEdges.size());
        log.debug("Merged size:"+merged.getVertexCount()+" nodes, "+merged.getEdgeCount()+"  edges");
        log.debug("edgemap size "+edgemap.size());
        log.debug("nodeset size "+nodeset.size());
        //At this point:
        //newedges should contain just those edges that are unshared between the two graphs
        //merged contains the merged network
        //edgemap is a map of pairs of nodes to edges in merged
        //nodeset is just a set of nodes

        log.info("Done merging networks");

        HashMap<Clique, Clique> unsharedCliques = new HashMap<Clique, Clique>();
        HashMap<Clique, Clique> unsharedCliques2 = new HashMap<Clique, Clique>();
        HashMap<Clique, Clique> sharedCliques = new HashMap<Clique, Clique>();
        Set<Set<String>> cliqueEdges = new HashSet<Set<String>>();
        int i = 0;

        UndirectedJungNetwork cliqueAdjacencyGraph = new UndirectedJungNetwork();

        for (Network cd : cdFrom) {
            Set<Clique> curr = new HashSet<Clique>();
            for (Clique c : ((CommunityFrame) cd).getCliques()) {
                Clique nc = new Clique(i++, c.getNodes());
                curr.add(nc);
                unsharedCliques.put(nc, nc);
                cliqueEdges.addAll(c.getEdgeSet());
            }
            addAdjacentCliques(curr, cliqueAdjacencyGraph);

        }
        log.debug("CAG: " + cliqueAdjacencyGraph.getVertexCount() + " nodes, "+cliqueAdjacencyGraph.getEdgeCount() + " edges");

        //cliqeuAdjacencyGraph is a graph of all cliques from cdFrom - representing existing communities
        //unsharedCliques contains all cliques from cdFrom
        //cliquedge contains all edges

        for (Network cd : cdTo) {
            Set<Clique> curr = new HashSet<Clique>();
            for (Clique c : ((CommunityFrame) cd).getCliques()) {

                Clique tomove = unsharedCliques.remove(c);
                if (tomove != null) {
                    curr.add(tomove);
                    sharedCliques.put(tomove, tomove);
                } else {
                    Clique nc = new Clique(i++, c.getNodes());
                    curr.add(nc);
                    //maybe not necessary?
                    unsharedCliques2.put(nc, nc);
                    cliqueEdges.addAll(nc.getEdgeSet());

                }

            }
            addAdjacentCliques(curr, cliqueAdjacencyGraph);
        }
          log.debug("CAG: " + cliqueAdjacencyGraph.getVertexCount() + " nodes, "+cliqueAdjacencyGraph.getEdgeCount() + " edges");
        //cliqueAdjacencyGraph now is a graph of all cliques from merging cdFrom & cdTo
        //sharedCliques contains cliques in both networks
        // /unsharedCliques (1 & 2) contains cliques unique to each of the two graphs respectively
        //cliqueEdges are all edges in any clique in either graph

        //prune edges we already know about
        newEdges.removeAll(cliqueEdges);
        //now newedges is just those edges unshared between the two graphs that are not in any clique;
        //these are the ones we need to look at


        //prune edges connecting nodes of insufficient degree
        for (Iterator<Set<Node>> it = newEdges.iterator(); it.hasNext(); ) {
            Set<Node> s = it.next();
            for (Node n : s) {
                if (merged.degree(n) < k - 1) {
                    it.remove();
                    break;
                }

            }

            // findCliques(edgemap.get(s))


        }
        log.debug("Found " + newEdges.size() + " edges to be processed");

        Set<Clique> ncliques = new HashSet<Clique>();
        for (Set<Node> edge : newEdges) {
            Edge e = edgemap.get(edge);
            List<Node> l = Arrays.asList(merged.getEndpoints(e).getFirst(), merged.getEndpoints(e).getSecond());
            ncliques.addAll(NetworkUtils.findCliquesContaining(merged, new HashSet<Node>(l), k));
        }

        log.debug("Found " + ncliques.size() + " new cliques");
        log.debug("CAG: " + cliqueAdjacencyGraph.getVertexCount() + " nodes, "+cliqueAdjacencyGraph.getEdgeCount() + " edges");
        //build linkes between any cliques that may need to be connected
        //brute force, could simplify
        for (Clique u1 : unsharedCliques.keySet()) {
            for (Clique u2 : unsharedCliques2.keySet()) {
                if (u1.overlap(u2) >= k - 1) {
                    Node n1 = cliqueAdjacencyNodeMap.get(u2);
                    Node n2 = cliqueAdjacencyNodeMap.get(u1);
                   // log.debug("Adding new edge between cliques");
                    cliqueAdjacencyGraph.addEdge(n1, n2, 1.0f);
                }
            }
        }

        log.debug("CAG: " + cliqueAdjacencyGraph.getVertexCount() + " nodes, "+cliqueAdjacencyGraph.getEdgeCount() + " edges");
        //add new cliques to graph and build any necessary connections
        //brute force, could simplify
        for (Clique c : ncliques) {
            Node nnode = new NodeImpl(c.getId() + "");
            cliqueAdjacencyGraph.addVertex(nnode);
            nnode.setProperty("clique", c);
            cliqueAdjacencyNodeMap.put(c, nnode);

            for (Node enode : cliqueAdjacencyGraph.getVertices()) {
                if (enode == nnode) continue;
                Clique e = (Clique) enode.getProperty("clique");
                if (e.overlap(c) >= k - 1) {
                   // log.debug("Adding new edge with emergent clique");
                    cliqueAdjacencyGraph.addEdge(enode, nnode, 1.0f);

                }
            }
        }
        log.debug("CAG: " + cliqueAdjacencyGraph.getVertexCount() + " nodes, "+cliqueAdjacencyGraph.getEdgeCount() + " edges");

        //the clique adjacency graph now has all communities in the merged graph; extract all connected components

        WeakComponentClusterer<Node, Edge> components = new WeakComponentClusterer<Node, Edge>();
        Set<Set<Node>> comps = components.transform(cliqueAdjacencyGraph);

        log.debug("Found "+comps.size()+" components");

        Set<Network> result = new HashSet<Network>();
        Set<Node> nodes = new HashSet<Node>(cliqueAdjacencyGraph.getVertices());
        Set<Clique> cliques = new HashSet<Clique>();
        //System.out.println("Identified "+comps.size()+" components ");
        for (Set<Node> community : comps) {
            UndirectedJungNetwork d = new UndirectedJungNetwork();
            for (Node n : community) {
                Clique c = (Clique) n.getProperty("clique");
                cliques.add(c);
                // d.addClique(c);
                nodes.remove(n);

                for (Set<String> e : c.getEdgeSet()) {
                    String[] s = e.toArray(new String[2]);
                    d.addEdge(new NodeImpl(s[0]), new NodeImpl(s[1]), 1.0f);
                }
            }
            result.add(d);
        }

        log.info(nodes.size() + " of " + cliqueAdjacencyGraph.getVertexCount() + " cliques unaccounted for");
        log.info("Found " + cliques.size() + " unique cliques in " + result.size() + "components");
        return new ArrayList<Network>(result);


    }

    //build a graph of adjacent cliques
    public void addAdjacentCliques(Set<Clique> cliques, UndirectedJungNetwork graph) {
        List<Clique> cliqit = new ArrayList<Clique>(cliques);
        for (int i = 0; i < cliqit.size(); i++) {
            Clique c = cliqit.get(i);
            Node n = cliqueAdjacencyNodeMap.get(c);
            if (n == null) {
                n = new NodeImpl(c.getId() + "");
                n.setProperty("clique", c);
                graph.addVertex(n);
                cliqueAdjacencyNodeMap.put(c, n);

            }
            for (int j = i + 1; j < cliqit.size(); j++) {
                Clique c2 = cliqit.get(j);
                Node n2 = cliqueAdjacencyNodeMap.get(c2);
                if (n2 == null) {
                    n2 = new NodeImpl(c2.getId() + "");
                    n2.setProperty("clique", c2);
                    graph.addVertex(n2);
                    cliqueAdjacencyNodeMap.put(c2, n2);

                }

                if (graph.findEdge(n, n2) == null && c.overlap(c2) >= k - 1) graph.addEdge(n, n2, 1.0f);

            }

        }

    }

    public static void main(String args[]) throws CommunityFinderException, IOException {
        TevaParameters param = new TevaParameters();
        param.setProperty(TevaParameters.WORKING_DIRECTORY, "../REASONTEVA/work");

       // for (int i = 0; i < 30; i++) {


            CFinderCommunityFinder finder = new CFinderCommunityFinder(false, false, new TevaParameters());
            File one = new File("../REASONTEVA/work/CFinderNetwork.TEvA.0.net");
            File two = new File("../REASONTEVA/work/CFinderNetwork.TEvA.1.net");

            Network none = NetworkUtils.readNetworkFile(one);
            Network ntwo = NetworkUtils.readNetworkFile(two);

            List<CommunityFrame> frames1 = finder.findCommunities(one, 4, 0);
            List<CommunityFrame> frames2 = finder.findCommunities(two, 4, 1);

            FastMergeStrategy strategy = new FastMergeStrategy(4);
            List<Network> result = strategy.process(none, frames1, ntwo, frames2, 0);
            for (Network n : result) {
                //log.info("Network: " + n.getNodes());
            }
       // }


    }


}
