package edu.mit.cci.teva.serialization;

import edu.mit.cci.teva.engine.CommunityModel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/9/12
 * Time: 11:28 AM
 */

@XmlRootElement(name = "map")
public class MapJaxbAdapter extends XmlAdapter<MapJaxbAdapter,Map<Integer,Set<CommunityModel.Connection>>> {


    @XmlElementWrapper(name = "data")
    List<IntConnectionPair> map = new ArrayList<IntConnectionPair>();

    public MapJaxbAdapter() {}

    public MapJaxbAdapter(Map<Integer,Set<CommunityModel.Connection>> kvMap) {
      for (Map.Entry<Integer,Set<CommunityModel.Connection>> ent:kvMap.entrySet()) {
          map.add(new IntConnectionPair(ent.getKey(),ent.getValue()));
      }
    }

    @Override
    public Map<Integer,Set<CommunityModel.Connection>> unmarshal(MapJaxbAdapter kvHashMap) throws Exception {
        return kvHashMap.toMap();
    }

    private Map<Integer,Set<CommunityModel.Connection>> toMap() {
        Map<Integer,Set<CommunityModel.Connection>> result = new HashMap<Integer,Set<CommunityModel.Connection>>();
        for (IntConnectionPair ent:map) {
            result.put(ent.key,new HashSet<CommunityModel.Connection>(ent.val));
        }
        return result;
    }

    @Override
    public MapJaxbAdapter marshal(Map<Integer, Set<CommunityModel.Connection>> kvMap) throws Exception {
        return new MapJaxbAdapter(kvMap);
    }

    @XmlRootElement(name = "pair")
    public static class IntConnectionPair {

        @XmlElement
        Integer key;
        @XmlElementWrapper(name = "targets")
        List<CommunityModel.Connection> val;

        public IntConnectionPair(Integer key, Set<CommunityModel.Connection> val) {
            this.key = key;
            this.val = new ArrayList<CommunityModel.Connection>(val);
        }

        public IntConnectionPair() {}
    }
}
