package edu.mit.cci.sna.impl;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Node;
import org.apache.commons.collections15.Factory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 4/20/11
 * Time: 2:26 PM
 */

@XmlRootElement(name="Node")
@XmlAccessorType(XmlAccessType.NONE)
public class EdgeImpl implements Edge {

    private static int idgen = 0;

    private float weight = 0;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private boolean directed = false;

    private Node[] endpoints;




    private EdgeImpl() {
        //throw new RuntimeException("Fix any clases that depend upon the no-arg constructor in EdgeImpl");
    }

    public EdgeImpl(Node from, Node to) {
        this(from, to, 1.0f, false);
    }

    public EdgeImpl(Node from, Node to, float weight, boolean directed) {

        this.directed = directed;
        this.setWeight(weight);
        this.endpoints = new Node[]{from, to};
    }

    @XmlAttribute(name = "weight")
    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public boolean equals(Object o) {
        if (o instanceof Edge) {
            Edge e = (Edge) o;
            return ((getEndpoints()[0].equals(e.getEndpoints()[0]) && e.getEndpoints()[1].equals(e.getEndpoints()[1])) ||
                    (!e.isDirected() && !isDirected() && (getEndpoints()[1].equals(e.getEndpoints()[0]) && getEndpoints()[0].equals(e.getEndpoints()[1]))));
        }
        return false;
    }

    public int hashCode() {
        return (getEndpoints()[0].hashCode() + getEndpoints()[1].hashCode()) * 7 + 13;
    }


    public boolean isDirected() {
        return directed;
    }


    public void setProperty(String key, Object val) {
        attributes.put(key, val);
    }

    @XmlElementWrapper(name = "endpoints")
    @XmlElement(name = "node")
    public Node[] getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Node[] endpoints) {
        this.endpoints = endpoints;
    }



    public Map<String, Object> getProperties() {
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
        return getEndpoints()[0] + "->" + getEndpoints()[1] + "(" + getWeight() + ")";
    }


    public static class JaxbAdapter extends XmlAdapter<EdgeImpl, Edge> {

        @Override
        public Edge unmarshal(EdgeImpl edge) throws Exception {
            return edge;
        }

        @Override
        public EdgeImpl marshal(Edge edge) throws Exception {
            //TODO generalize this
            return (EdgeImpl) edge;
        }
    }
}
