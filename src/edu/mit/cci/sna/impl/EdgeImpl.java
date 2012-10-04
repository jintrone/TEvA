package edu.mit.cci.sna.impl;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Node;
import org.apache.commons.collections15.Factory;

import java.util.HashMap;
import java.util.Map;

/**
* User: jintrone
* Date: 4/20/11
* Time: 2:26 PM
*/
public class EdgeImpl implements Edge {

    private static int idgen = 0;

    private float weight = 0;

    private Map<String,Object> attributes = new HashMap<String,Object>();

    private boolean directed = false;

    private Node[] endpoints;


    private EdgeImpl() {
        throw new RuntimeException("Fix any clases that depend upon the no-arg constructor in EdgeImpl");
    }

    public EdgeImpl(Node from, Node to) {
        this(from,to,1.0f,false);
    }

    public EdgeImpl(Node from, Node to, float weight, boolean directed) {

        this.directed = directed;
        this.setWeight(weight);
        this.endpoints = new Node[] {from,to};
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public boolean equals(Object o) {
            if (o instanceof Edge) {
                Edge e = (Edge)o;
                return ((getEndpoints()[0].equals(e.getEndpoints()[0]) && e.getEndpoints()[1].equals(e.getEndpoints()[1])) ||
                        (!e.isDirected()&& !isDirected() && (getEndpoints()[1].equals(e.getEndpoints()[0]) && getEndpoints()[0].equals(e.getEndpoints()[1]))));
            }
            return false;
        }

        public int hashCode() {
            return (getEndpoints()[0].hashCode()+getEndpoints()[1].hashCode()) * 7 +13;
        }



    public boolean isDirected() {
        return directed;
    }


    public void setProperty(String key, Object val) {
        attributes.put(key,val);
    }

    public Node[] getEndpoints() {
         return endpoints;
    }


    public Map<String,Object> getProperties() {
        return attributes;
    }

    public Object getProperty(String key) {
       return attributes.get(key);
    }

    public static Factory<EdgeImpl> getFactory() {
        return new Factory<EdgeImpl>() {

            public EdgeImpl create() {
                return new EdgeImpl();
            }
        };
    }



   public String toString() {
            return getEndpoints()[0]+"->"+getEndpoints()[1]+"("+getWeight()+")";
        }

}
