package edu.mit.cci.teva.util;

import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;

import java.util.*;

/**
 * User: jintrone
 * Date: 5/14/13
 * Time: 1:25 PM
 */
public class TimeWindowDiscussionThreadAdapter implements DiscussionThread {

    private final DiscussionThread thread;

    private List<Post> result = null;

    private Date start;
    private Date end;

    public TimeWindowDiscussionThreadAdapter(DiscussionThread t, Date start, Date end) {
        this.thread = t;
        this.start = start;
        this.end = end;
    }

    public List<Post> getPosts() {


        if (result == null) {
            result = new ArrayList<Post>(thread.getPosts());

            for (Iterator<Post> p =result.iterator();p.hasNext();) {
                Post post = p.next();
                if (start!=null && start.after(post.getTime())) {
                    p.remove();
                    continue;
                }
                if (end!=null && end.before(post.getTime())) {
                    p.remove();

                }
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
