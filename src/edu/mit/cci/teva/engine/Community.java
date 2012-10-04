package edu.mit.cci.teva.engine;


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
public class Community {

    public static int ID_COUNT = 0;

    public Map<Integer, CommunityFrame> history = new HashMap<Integer, CommunityFrame>();

    public int id;

    public boolean expired = false;

    public boolean isExpired() {
        return expired;
    }

    public void expire() {
        this.expired = true;
    }


    public int maxbin = -1;
    public int minbin = -1;

    public static void reset() {
        ID_COUNT = 0;
    }

    public int getId() {
        return id;
    }

    public Community() {
        this(ID_COUNT++);

    }

    public Community(int id) {
        this.id = id;
        ID_COUNT = Math.max(ID_COUNT, id);

    }


    public void addFrame(CommunityFrame frame) {
        frame.setCommunity(this);
        history.put(frame.getWindow(), frame);
        maxbin = Math.max(frame.getWindow(), maxbin);
        minbin = minbin == -1 ? frame.getWindow() : Math.min(frame.getWindow(), minbin);
    }


    public int getMaxBin() {
        return maxbin;
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

    private List<String> representation = null;

//    public List<String> getMaximalRepresentation() {
//       if (representation == null) {
//           representation = Utils.getCommunityRepresentation(maximal,5);
//       }
//        return representation;
//    }


}
