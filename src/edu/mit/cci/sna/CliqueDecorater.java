package edu.mit.cci.sna;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/3/12
 * Time: 10:58 PM
 */
public class CliqueDecorater implements CliqueDecoratedNetwork {

    public void add(Edge e) {
        network.add(e);
    }

    public void add(Node n) {
        network.add(n);
    }

    private Network network;
    private Set<Clique> cliques = new HashSet<Clique>();

    public CliqueDecorater(Network n) {
       this.network  = n;
    }

    public Collection<Edge> getEdges() {
        return network.getEdges();
    }

    public Collection<Node> getNodes() {
        return network.getNodes();
    }

    public boolean isDirected() {
        return network.isDirected();
    }

    public void remove(Edge e) {
        network.remove(e);
    }

    public void remove(Node n) {
        network.remove(n);
    }


    public Set<Clique> getCliques() {
        return cliques;
    }

    public void addCliques(Collection<Clique> cliques) {
        this.cliques.addAll(cliques);
    }


}
