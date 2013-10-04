package edu.mit.cci.teva.util;

import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 5/14/13
 * Time: 1:25 PM
 */
public class LeafPruningDiscussionThreadAdapter implements DiscussionThread {

    private final DiscussionThread thread;

    private List<Post> result = null;

    public LeafPruningDiscussionThreadAdapter(DiscussionThread t) {
        this.thread = t;
    }

    public List<Post> getPosts() {


        if (result == null) {
            result = new ArrayList<Post>();
            Map<String, Post> postmap = new HashMap<String, Post>();
            Map<Post, Integer> map = new LinkedHashMap<Post, Integer>();
            for (Post p : thread.getPosts()) {
                map.put(p, 0);
                postmap.put(p.getPostid(), p);
                if (p.getReplyToId() != null) {
                    map.put(postmap.get(p.getReplyToId()), map.get(postmap.get(p.getReplyToId())) + 1);
                }
            }
            for (Map.Entry<Post, Integer> ent : map.entrySet()) {
                if (ent.getValue() == 0) continue;
                result.add(ent.getKey());
            }

        }
        return result;
    }

    public List<String> getSentiment(Post p) {
        return thread.getSentiment(p);
    }

    public String getThreadId() {
        return thread.getThreadId();
    }
}
