package edu.mit.cci.teva.engine;


import edu.mit.cci.teva.serialization.CommunityFrameMapJaxbAdapter;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/1/12
 * Time: 10:32 PM
 */

@XmlRootElement(name = "Community")
@XmlAccessorType(XmlAccessType.NONE)
public class Community {

    public static int ID_COUNT = 0;


    @XmlElement(name = "assignment")
    private List<ConversationChunk> assignments = new ArrayList<ConversationChunk>();


    @XmlJavaTypeAdapter(CommunityFrameMapJaxbAdapter.class)
    public Map<Integer, CommunityFrame> history = new HashMap<Integer, CommunityFrame>();

    @XmlID
    public String id;

    public boolean expired = false;

    public boolean isExpired() {
        return expired;
    }

    public void expire() {
        this.expired = true;
    }


    private int maxbin = -1;
    private int minbin = -1;

    public static void reset() {
        ID_COUNT = 0;
    }

    public String getId() {
        return id;
    }

    private Community() {


    }

    public static Community create() {
        return new Community(ID_COUNT++);
    }

    public Community(int id) {
        this.id = id+"";
        ID_COUNT = Math.max(ID_COUNT, id);

    }


    public void addFrame(CommunityFrame frame) {
        frame.setCommunity(this);
        history.put(frame.getWindow(), frame);
        maxbin = Math.max(frame.getWindow(), maxbin);
        minbin = minbin == -1 ? frame.getWindow() : Math.min(frame.getWindow(), minbin);
    }


    public int getMaxBin() {
        if (maxbin == -1 && !history.isEmpty()) {
            for (int key:history.keySet()) {
                maxbin = Math.max(maxbin,key);
            }
        }
        return maxbin;
    }

    public int getMinBin() {
       if (maxbin == -1 && !history.isEmpty()) {
            for (int key:history.keySet()) {
                minbin = (minbin==-1)?key:Math.min(minbin, key);
            }
        }
        return minbin;
    }


    public CommunityFrame getCommunityAtBin(int i) {
        return history.get(i);
    }

    public String getName() {
        return "Community" + id;
    }

//    public double bestSimilarity(Collection<String> words) {
//        double max = 0;
//        for (CommunityFrame snapshot : history.values()) {
//            max = Math.max(max, similarity(snapshot.nodes, words));
//        }
//        return max;
//    }

//    public double similarity(Community other) {
//        return similarity(getMaximalNodeset(),other.getMaximalNodeset());
//    }

    public static double similarity(Collection<String> from, Collection<String> to) {
        double count = 0;
        for (String s : to) {
            if (from.contains(s)) {
                count++;
            }
        }

        Set<String> total = new HashSet<String>(from);
        total.addAll(to);
        return count / (double) total.size();
    }


    public String toString() {
        return getName();
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (CommunityFrame f:history.values()) {
            f.setCommunity(this);
        }
    }

    public boolean equals(Community c) {
        return this.getId().equals(c.getId()) && history.values().equals(c.history.values());
    }


    public void addAssignment(ConversationChunk chunk) {
      assignments.add(chunk);
    }

//    public List<String> getMaximalRepresentation() {
//       if (representation == null) {
//           representation = Utils.getCommunityRepresentation(maximal,5);
//       }
//        return representation;
//    }


    public List<ConversationChunk> getAssignments() {
        return assignments;
    }
}
