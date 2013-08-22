package edu.mit.cci.sna;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: jintrone
 * Date: 6/19/12
 * Time: 6:54 AM
 */
@XmlRootElement(name = "Clique")
@XmlAccessorType(XmlAccessType.NONE)
public class Clique {

    private static int MAX_ID = -1;

    private int id;
    private Set<String> nodes = new HashSet<String>();


    public Clique() {
        setId(MAX_ID + 1);
    }

//    public Clique(int capacity) {
//        setId(MAX_ID + 1);
//    }

    private void setId(int id) {
        this.id = id;
        MAX_ID = Math.max(id, MAX_ID);
    }

    public Clique(int localId) {
        setId(localId);
    }


    public Clique(int localId, Collection<String> nodes) {
        this.nodes.addAll(nodes);
        setId(localId);
    }

    @XmlAttribute(name = "id")
    public int getId() {
        return id;
    }

    public void addNode(String node) {
        this.nodes.add(node);
    }

    public void setNodes(Collection<String> nodes) {
        this.nodes.addAll(nodes);
    }

    public void setUsingNodes(Collection<Node> nodes) {
        for (Node n:nodes) {
            this.nodes.add(n.getLabel());
        }
    }

    @XmlElement(name = "node")
    public Set<String> getNodes() {
        return nodes;
    }

    public boolean equals(Object o) {
        return (o instanceof Clique && ((Clique) o).getNodes().equals(nodes));
    }

    public int hashCode() {
        return (nodes.hashCode() + Clique.class.hashCode()) * 7 - 13;
    }

    public int overlap(Clique c) {
        Set<String> nodes = new HashSet<String>(c.getNodes());
        nodes.retainAll(this.nodes);
        return nodes.size();
    }

    public Collection<Set<String>> getEdgeSet() {
        List<Set<String>> result = new ArrayList<Set<String>>();
        String[] s = nodes.toArray(new String[nodes.size()]);
        for (int i = 0; i < s.length; i++) {
            for (int j = i + 1; j < s.length; j++) {
                Set<String> pair = new HashSet<String>();
                result.add(pair);
                pair.add(s[i]);
                pair.add(s[j]);
            }
        }
        return result;
    }

    public String toString() {
        return "Clique " + id + ": " + getNodes();

    }

}
