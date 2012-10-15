package edu.mit.cci.teva.engine;




import edu.mit.cci.teva.serialization.MapJaxbAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 10:50 AM
 */

@XmlRootElement(name = "model")
public class CommunityModel {
    
    @XmlElement(name = "communities")
    List<Community> communities = new ArrayList<Community>();

    @XmlElement(name = "windows")
    Date[][] windows;

    @XmlElement(name = "parameters")
    TevaParameters parameters;

    @XmlJavaTypeAdapter(MapJaxbAdapter.class)
    @XmlElement(name="apawners")
    public Map<Integer,Set<Connection>> spawners = new HashMap<Integer,Set<Connection>>();

    @XmlJavaTypeAdapter(MapJaxbAdapter.class)
    @XmlElement(name="consumedBy")
    public Map<Integer,Set<Connection>> consumers = new HashMap<Integer,Set<Connection>>();

    @XmlJavaTypeAdapter(MapJaxbAdapter.class)
    @XmlElement(name="informs")
    public Map<Integer,Set<Connection>> informs = new HashMap<Integer,Set<Connection>>();

    @XmlElement(name = "corpus")
    private String corpusName;


    private CommunityModel() {

    }

    public CommunityModel(TevaParameters params, Date[][] windows, String corpusName) {
        this.windows = windows;
        this.parameters = params;
        this.corpusName = corpusName;

    }


    public void addConnection(int bin, float weight, ConnectionType type, Community from, Community to) {

        Set<Connection> conn = map(type).get(bin);
        if (conn == null) {
            map(type).put(bin,conn = new HashSet<Connection>());
        }
        conn.add(new Connection(from,to,weight));
    }

    public Set<Connection> getConnection(int bin, ConnectionType  type) {
        return map(type)!=null?map(type).get(bin): Collections.<Connection>emptySet();
    }

    public Date[][] getWindows() {
        return windows;
    }

    private Map<Integer,Set<Connection>> map(ConnectionType type) {
        if (type == ConnectionType.SPAWNS) {
            return spawners;
        } else if (type == ConnectionType.INFORMS) {
            return informs;
        } else if (type == ConnectionType.CONSUMES) {
            return consumers;
        }
        else return null;
    }

    public void addCommunity(Community c) {
        communities.add(c);
    }

    public List<Community> getCommunities() {
        return communities;
    }

    @XmlRootElement(name = "Connection")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Connection {

        @XmlAttribute
        public float weight;

        @XmlIDREF
        @XmlAttribute
        public Community source;

        @XmlIDREF
        @XmlAttribute
        public Community target;

        public Connection(){}


        public Connection(Community source, Community target, float weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        public boolean equals(Connection c) {
            return weight == c.weight && source == c.source && target == c.target;
        }

        public String toString() {
            return source.getId()+"->"+target.getId();
        }
    }

    public enum ConnectionType {

        SPAWNS, CONSUMES, INFORMS



    }

}
