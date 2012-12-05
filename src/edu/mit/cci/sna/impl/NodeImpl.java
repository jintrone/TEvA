package edu.mit.cci.sna.impl;

import edu.mit.cci.sna.Node;
import org.apache.commons.collections15.Factory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
* User: jintrone
* Date: 4/20/11
* Time: 2:25 PM
*/

@XmlRootElement(name="Node")
@XmlAccessorType(XmlAccessType.NONE)
public class NodeImpl implements Node {

    private static int idgen = 0;

    private Map<String,Object> attributes = new HashMap<String,Object>();


    private String label;


    private NodeImpl() {

    }

     public NodeImpl(String label) {
         setLabel(label);

    }

    public void setLabel(String label) {
        this.label = label;
    }
    @XmlAttribute(name = "id")
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
        return "N("+getId()+")";
    }

    public int hashCode() {
        return label.hashCode()*7+13;
    }

    public boolean equals(Object o) {
        return o instanceof Node && ((Node)o).getId().equals(getId());
    }

    public static class JaxbAdapter extends XmlAdapter<NodeImpl,Node> {

        @Override
        public Node unmarshal(NodeImpl node) throws Exception {
            return node;
        }

        @Override
        public NodeImpl marshal(Node node) throws Exception {
            //TODO generalize this
            return (NodeImpl)node;
        }
    }



}
