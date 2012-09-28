package edu.mit.cci.sna.impl;

import edu.mit.cci.sna.Node;
import org.apache.commons.collections15.Factory;

import java.util.HashMap;
import java.util.Map;

/**
* User: jintrone
* Date: 4/20/11
* Time: 2:25 PM
*/
public class NodeImpl implements Node {

    private static int idgen = 0;

    private Map<String,Object> attributes = new HashMap<String,Object>();


    private String label;



     public NodeImpl(String label) {
         setLabel(label);

    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return label;
    }

    public static Factory<NodeImpl> getFactory() {
        return new Factory<NodeImpl>() {

            public NodeImpl create() {
                return new NodeImpl(""+idgen++);
            }
        };
    }

    public void setProperty(String key, Object val) {
        attributes.put(key,val);
    }

    public Map<String,Object> getProperties() {
        return attributes;
    }

    public Object getProperty(String key) {
       return attributes.get(key);
    }


    public String toString() {
        return "Node("+getId()+") - "+getLabel();
    }

    public int hashCode() {
        return label.hashCode()*7+13;
    }

    public boolean equals(Object o) {
        return o instanceof NodeImpl && ((NodeImpl)o).getId() == getId();
    }



}
