package edu.mit.cci.adapters.csv;

import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
* Created by josh on 1/20/14.
*/
public class DiscussionThreadImpl implements DiscussionThread {

    private String threadid;
    private List<Post> posts = new ArrayList<Post>();
    private boolean sorted = false;


    public DiscussionThreadImpl(String threadid) {
       this.threadid = threadid;
    }


    public String getThreadId() {
       return threadid;
    }

    public void addPost(Post p) {
        ((PostImpl)p).setThreadId(this.getThreadId());
        this.posts.add(p);
        sorted = false;
    }

    public List<Post> getPosts() {
        if (!sorted) {
            Collections.sort(posts, new Comparator<Post>() {
                public int compare(Post post, Post post1) {
                    return post.getTime().compareTo(post1.getTime());
                }
            });
        }
        return posts;
    }

    public List<String> getSentiment(Post p) {
         return Collections.emptyList();
    }
}
