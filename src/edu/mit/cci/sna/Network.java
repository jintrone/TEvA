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

    public static abstract class DefaultEdge {

        public abstract String[] getEndpoints();

        public abstract float getWeight();

        public abstract void setWeight(float weight);

         public boolean equals(Object o) {
            if (o instanceof Edge) {
                Edge e = (Edge)o;
                return ((getEndpoints()[0].equals(e.getEndpoints()[0]) && e.getEndpoints()[1].equals(e.getEndpoints()[1])) ||
                        (getEndpoints()[1].equals(e.getEndpoints()[0]) && getEndpoints()[0].equals(e.getEndpoints()[1])));
            }
            return false;
        }

        public int hashCode() {
            return (getEndpoints()[0].hashCode()+getEndpoints()[1].hashCode()) * 7 +13;
        }

        public String toString() {
            return getEndpoints()[0]+"->"+getEndpoints()[1]+"("+getWeight()+")";
        }
    }

    public static interface Factory {

        public void addEdge(String[] ends, float weight);
        public Network getAdapter();
        public Network getAdapter(boolean b);
        public boolean hasData();

    }

}
