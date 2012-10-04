package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.sna.Node;

import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.util.U;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;

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


    int k = -1;

    private Map<Clique,Node> cliqueAdjacencyNodeMap = new HashMap<Clique,Node>();


    public FastMergeStrategy(int k) {

        this.k = k;

    }


    public List<Network> process(Network from, Collection<CommunityFrame> cdfrom, Network to, Collection<CommunityFrame> cdto) {
         cliqueAdjacencyNodeMap.clear();


        Set<Network> cdFrom = new HashSet<Network>(cdfrom);
        Set<Network> cdTo = new HashSet<Network>(cdto);

        UndirectedJungNetwork merged = new UndirectedJungNetwork();
        Set<Node> nodeset = new HashSet<Node>();
        Map<Set<Node>, Edge> edgemap = new HashMap<Set<Node>, Edge>();
        Set<Set<Node>> newEdges = new HashSet<Set<Node>>();

        //create new graph, separating shared edges from unshared edges during the merge process
        int fedges = from.getEdges().size();
        for (Edge e : U.multiIterator(from.getEdges(), to.getEdges())) {
            boolean second = fedges-- < 0;


            Set<Node> pair = new HashSet<Node>();
            pair.add(e.getEndpoints()[0]);
            pair.add(e.getEndpoints()[1]);

            if (!second) {
                newEdges.add(pair);
            } else {
                if (!newEdges.remove(pair)) {
                    newEdges.add(pair);
                }
            }

            Edge existing = edgemap.get(pair);
            if (existing == null) {
                Node node1=e.getEndpoints()[0],node2=e.getEndpoints()[1];
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
                if (e.getWeight() > existing.getWeight()) {
                    existing.setWeight(e.getWeight());
                }
            }

            edgemap.put(pair, existing);
        }

        HashMap<Clique, Clique> unsharedCliques = new HashMap<Clique, Clique>();
        HashMap<Clique, Clique> unsharedCliques2 = new HashMap<Clique, Clique>();
        HashMap<Clique, Clique> sharedCliques = new HashMap<Clique, Clique>();
        Set<Set<String>> cliqueEdges = new HashSet<Set<String>>();
        int i = 0;

    UndirectedJungNetwork cliqueAdjacencyGraph = new UndirectedJungNetwork();

        for (Network cd : cdFrom) {
            Set<Clique> curr = new HashSet<Clique>();
            for (Clique c : ((CommunityFrame)cd).getCliques()) {
                Clique nc = new Clique(i++, c.getNodes());
                curr.add(nc);
                unsharedCliques.put(nc, nc);
                cliqueEdges.addAll(c.getEdgeSet());
            }
            addAdjacentCliques(curr, cliqueAdjacencyGraph);

        }

        for (Network cd : cdTo) {
            Set<Clique> curr = new HashSet<Clique>();
            for (Clique c : ((CommunityFrame)cd).getCliques()) {

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

        //prune edges we already know about
        newEdges.removeAll(cliqueEdges);

        //prune edges with insufficient degree
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

        Set<Clique> ncliques = new HashSet<Clique>();
        for (Set<Node> edge:newEdges) {
            Edge e = edgemap.get(edge);
            List<Node> l = Arrays.asList(merged.getEndpoints(e).getFirst(),merged.getEndpoints(e).getSecond());
            ncliques.addAll(NetworkUtils.findCliquesContaining(merged, new HashSet<Node>(l), k));
        }

        //brute force, could simplify
        for (Clique u1:unsharedCliques.keySet()) {
             for (Clique u2:unsharedCliques2.keySet()) {
                 if (u1.overlap(u2) >= k-1) {
                     Node n1 = cliqueAdjacencyNodeMap.get(u2);
                     Node n2 = cliqueAdjacencyNodeMap.get(u1);
                     cliqueAdjacencyGraph.addEdge(n1,n2,1.0f);
                 }
             }
        }

        //brute force, could simplify
        for (Clique c:ncliques) {
            Node nnode = new NodeImpl(c.getId() + "");
            cliqueAdjacencyGraph.addVertex(nnode);
            nnode.setProperty("clique", c);
            for (Node enode:cliqueAdjacencyGraph.getVertices()) {
                if (enode == nnode) continue;
                Clique e = (Clique) enode.getProperty("clique");
                if (e.overlap(c)>=k-1) {
                    cliqueAdjacencyGraph.addEdge(enode,nnode,1.0f);

                }
            }
        }

        WeakComponentClusterer<Node, Edge> components = new WeakComponentClusterer<Node, Edge>();
        Set<Set<Node>> comps = components.transform(cliqueAdjacencyGraph);

        Set<Network> result = new HashSet<Network>();
        Set<Node> nodes = new HashSet<Node>(cliqueAdjacencyGraph.getVertices());
        Set<Clique> cliques = new HashSet<Clique>();
        System.out.println("Identified "+comps.size()+" components ");
        for (Set<Node> community:comps) {
            UndirectedJungNetwork d = new UndirectedJungNetwork();
            for (Node n:community) {
                Clique c = (Clique) n.getProperty("clique");
                cliques.add(c);
                // d.addClique(c);
                nodes.remove(n);
                for (Set<String> e:c.getEdgeSet()) {
                     String[] s = e.toArray(new String[2]);
                     d.addEdge(new NodeImpl(s[0]),new NodeImpl(s[1]),1.0f);
                }
            }
            result.add(d);
        }

        System.out.println(nodes.size()+" of "+cliqueAdjacencyGraph.getVertexCount()+" cliques unaccounted for");
        System.out.println("Found "+cliques.size()+" unique cliques in components");
        return new ArrayList<Network>(result);





    }


    public void addAdjacentCliques(Set<Clique> cliques, UndirectedJungNetwork graph) {
        List<Clique> cliqit = new ArrayList<Clique>(cliques);
        for (int i = 0; i < cliqit.size(); i++) {
            Clique c = cliqit.get(i);
            Node n = cliqueAdjacencyNodeMap.get(c);
            if (n == null) {
                n = new NodeImpl(c.getId() + "");
                n.setProperty("clique", c);
                graph.addVertex(n);
                cliqueAdjacencyNodeMap.put(c,n);

            }
            for (int j = i+1; j < cliqit.size(); j++) {
                Clique c2 = cliqit.get(j);
                Node n2 = cliqueAdjacencyNodeMap.get(c2);
                if (n2 == null) {
                    n2 = new NodeImpl(c2.getId()+"");
                    n2.setProperty("clique", c2);
                    graph.addVertex(n2);
                    cliqueAdjacencyNodeMap.put(c2,n);

                }

                if (graph.findEdge(n, n2) == null && c.overlap(c2) >= k - 1) graph.addEdge(n, n2, 1.0f);

            }

        }

    }


}
