package edu.mit.cci.sna;

import java.util.Collection;
import java.util.Set;

/**
 * Simple wrapper around a generic network
 *
 * User: jintrone
 * Date: 4/19/12
 * Time: 3:46 PM
 */
public interface Network {

    public Collection<Node> getNodes();

    public Collection<Edge> getEdges();

    public boolean isDirected();

    public void remove(Edge e);

    public void remove(Node n);

    public void add(Node n);

    public void add(Edge e);

    public static interface Factory {

        public void addEdge(String[] ends, float weight);
        public Network getAdapter();
        public Network getAdapter(boolean b);
        public boolean hasData();

    }

}
