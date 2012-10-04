package edu.mit.cci.teva.engine;




import java.util.ArrayList;
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
public class CommunityModel {
    
    
    List<Community> communities = new ArrayList<Community>();
    
    Date[][] windows;
    
    public Map<Integer,Set<Connection>> spawners = new HashMap<Integer,Set<Connection>>();

    public Map<Integer,Set<Connection>> consumers = new HashMap<Integer,Set<Connection>>();

    public Map<Integer,Set<Connection>> informs = new HashMap<Integer,Set<Connection>>();
    
    
    public void addConnection(int bin, float weight, ConnectionType type, Community from, Community to) {

        Set<Connection> conn = type.map().get(bin);
        if (conn == null) {
            type.map().put(bin,conn = new HashSet<Connection>());
        }
        conn.add(new Connection(from,to,weight));
    }

    public void addCommunity(Community c) {
        communities.add(c);
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public static class Connection {

        public float weight;
        public Community source;
        public Community target;

        public Connection(Community source, Community target, float weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }
    }

    public enum ConnectionType {

        SPAWNS, CONSUMS, INFORMS;



        Map<Integer,Set<Connection>> map;

        ConnectionType() {
            map = new HashMap<Integer, Set<Connection>>();
        }

        public Map<Integer,Set<Connection>> map() {
            return map;
        }


    }

}
