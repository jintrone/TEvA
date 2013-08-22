package edu.mit.cci.teva.util;

import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.engine.ConversationChunk;
import edu.mit.cci.text.windowing.Windowable;

import java.util.*;

/**
 * User: jintrone
 * Date: 5/13/13
 * Time: 3:32 PM
 * <p/>
 * Provides a number of useful lookup functions to help map between a message and the CommunityModel
 */
public class MessageToModelMap {

    Map<String, MessageMeta> data = new HashMap<String, MessageMeta>();

    Map<String, Set<MessageMeta>> communityToMessageMap = new HashMap<String, Set<MessageMeta>>();

    Map<String, Community> communityMap = new HashMap<String, Community>();


    private final CommunityModel model;


    public MessageToModelMap(CommunityModel model) {


        this.model = model;
        for (Community c : model.getCommunities()) {
            communityMap.put(c.getId(), c);
            if (!communityToMessageMap.containsKey(c.getId())) {
                communityToMessageMap.put(c.getId(), new HashSet<MessageMeta>());
            }
            for (ConversationChunk chunk : c.getAssignments()) {
                for (Windowable w : chunk.messages) {

                    if (!data.containsKey(w.getId())) {
                        MessageMeta m = new MessageMeta(w.getId(), w.getStart());
                        data.put(w.getId(), m);
                        communityToMessageMap.get(c.getId()).add(m);

                    }
                    data.get(w.getId()).addChunk(c.getId(), chunk);
                }

            }
        }

    }

    public CommunityWindow getBestCommunityWindow(String messageId) {
        if (!data.containsKey(messageId)) return null;
        else {
            MessageMeta meta = data.get(messageId);
            String community = null;
            ConversationChunk chunk = null;
            for (Map.Entry<String, List<ConversationChunk>> ent : meta.communityAppearances.entrySet()) {
                if (community == null) {
                    community = ent.getKey();
                    chunk = ent.getValue().get(0);
                }
                else {
                    for (ConversationChunk c:ent.getValue()) {
                        if (c.coverage > chunk.coverage ||
                                (c.coverage == chunk.coverage && c.similarity > chunk.similarity)) {
                            chunk = c;
                            community = ent.getKey();
                        }
                    }


                }
            }
            return new CommunityWindow(community,chunk.window);
        }
    }



    public Map.Entry<String, List<ConversationChunk>> _getMostRepresentativeCommunity(String messageId) {
        if (!data.containsKey(messageId)) return null;
        else {
            MessageMeta meta = data.get(messageId);
            Map.Entry<String, List<ConversationChunk>> best = null;
            for (Map.Entry<String, List<ConversationChunk>> ent : meta.communityAppearances.entrySet()) {
                if (best == null || (ent.getValue().size() > best.getValue().size())) {
                    best = ent;
                }
            }
            return best;
        }
    }

    public String getMostRepresentativeCommunity(String msgid) {
        return _getMostRepresentativeCommunity(msgid).getKey();
    }

    /**public CommunityWindow getClosestCommunityWindow(String messageId) {
        if (!data.containsKey(messageId)) return null;
        else {
            MessageMeta meta = data.get(messageId);
            Map<String,ConversationChunk> chunks = new HashMap<String, ConversationChunk>();
            for (Map.Entry<String, List<ConversationChunk>> ent : meta.communityAppearances.entrySet()) {
                if (!chunks.containsKey(ent.getKey())) {
                    chunks.put(ent.getKey());
                }
            }
            return best;
        }
    }
     **/


    public CommunityWindow placeMessageInLastWindow(String messageId) {
        if (!data.containsKey(messageId)) return null;
        Map.Entry<String, List<ConversationChunk>> ent = _getMostRepresentativeCommunity(messageId);
        ConversationChunk best = null;
        for (ConversationChunk chunk : ent.getValue()) {
            if (best == null || chunk.window > best.window) {
                best = chunk;
            }
        }
        return new CommunityWindow(ent.getKey(), best.window);
    }

    public CommunityWindow placeMessageInFirstWindow(String messageId) {
        if (!data.containsKey(messageId)) return null;
        Map.Entry<String, List<ConversationChunk>> ent = _getMostRepresentativeCommunity(messageId);
        ConversationChunk best = null;
        for (ConversationChunk chunk : ent.getValue()) {
            if (best == null || chunk.window < best.window) {
                best = chunk;
            }
        }
        return new CommunityWindow(ent.getKey(), best.window);
    }

    public CommunityWindows getAllWindowsForBestCommunity(String messageId) {
        if (!data.containsKey(messageId)) return null;
        Map.Entry<String, List<ConversationChunk>> ent = _getMostRepresentativeCommunity(messageId);
        CommunityWindows result = new CommunityWindows(ent.getKey());
        for (ConversationChunk chunk : ent.getValue()) {
            result.add(chunk.window);
        }
        return result;
    }

    public List<? extends CommunityWindow> getRankedCommunityWindows(String messageId) {
        if (!data.containsKey(messageId)) return null;
        List<WeightedCommunityWindow> result = new ArrayList<WeightedCommunityWindow>();
        MessageMeta meta = data.get(messageId);
        for (Map.Entry<String,List<ConversationChunk>> ent:meta.communityAppearances.entrySet()) {
            for (ConversationChunk chunk:ent.getValue()){
                result.add(new WeightedCommunityWindow(ent.getKey(),chunk.window,chunk.similarity,chunk.coverage));
            }
        }
        Collections.sort(result, new Comparator<WeightedCommunityWindow>() {
            public int compare(WeightedCommunityWindow o1, WeightedCommunityWindow o2) {
                if (o1.coverage > o2.coverage) {
                    return -1;
                } else if (o1.coverage == o2.coverage) {
                    if (o1.similarity > o2.similarity) {
                        return -1;
                    }  else if (o1.similarity == o2.similarity) {
                        return 0;
                    }  else return 1;
                } else return 1;

            }
        });
        return result;
    }


    private Set<MessageMeta> _findMessagesMetaAt(String community, int window) {
        Set<MessageMeta> meta = communityToMessageMap.get(community);
        Set<MessageMeta> messages = new HashSet<MessageMeta>();
        for (MessageMeta m : meta) {
            if (m.windowAppearances.containsKey(window)) {
                messages.add(m);
            }

        }
        return messages;
    }

    private Set<String> findeMessagesAt(String community, int window) {
        Set<MessageMeta> meta = _findMessagesMetaAt(community, window);
        Set<String> result = new HashSet<String>();
        for (MessageMeta m : meta) {
            result.add(m.msgid);
        }
        return result;
    }

    public String getLastMessageAt(String community, int window) {
        Set<MessageMeta> meta = _findMessagesMetaAt(community, window);
        MessageMeta result = null;
        for (MessageMeta m : meta) {
            if (result == null || result.time.before(m.time)) {
                result = m;
            }
        }
        return result.msgid;
    }

    public String getFirstMessageAt(String community, int window) {
        Set<MessageMeta> meta = _findMessagesMetaAt(community, window);
        MessageMeta result = null;
        for (MessageMeta m : meta) {
            if (result == null || result.time.after(m.time)) {
                result = m;
            }
        }
        return result.msgid;
    }


    private static class MessageMeta {

        Map<String, List<ConversationChunk>> communityAppearances = new HashMap<String, List<ConversationChunk>>();
        Map<Integer, List<CommunityConversationChunk>> windowAppearances = new TreeMap<Integer, List<CommunityConversationChunk>>();
        String msgid = null;
        Date time;

        MessageMeta(String msgid, Date time) {
            this.msgid = msgid;
            this.time = time;
        }

        public void addChunk(String community, ConversationChunk chunk) {
            if (!communityAppearances.containsKey(community)) {
                communityAppearances.put(community, new ArrayList<ConversationChunk>());

            }
            communityAppearances.get(community).add(chunk);
            if (!windowAppearances.containsKey(chunk.window)) {
                windowAppearances.put(chunk.window, new ArrayList<CommunityConversationChunk>());

            }
            windowAppearances.get(chunk.window).add(new CommunityConversationChunk(chunk, community));

        }
    }

    private static class WeightedCommunityWindow extends CommunityWindow {

        float similarity;
        float coverage;

        public WeightedCommunityWindow(String community, int window, float similarity, float coverage) {
            super(community, window);
            this.similarity = similarity;
            this.coverage = coverage;
        }


    }

    private static class CommunityConversationChunk extends ConversationChunk {

        private final ConversationChunk chunk;

        public String community;

        public CommunityConversationChunk(ConversationChunk chunk, String community) {
            super();
            this.chunk = chunk;
            this.community = community;
        }


    }

}
