package edu.mit.cci.teva.model;

import java.util.List;

/**
 * User: jintrone
 * Date: 8/10/11
 * Time: 5:28 PM
 */
public interface DiscussionThread {

    public String getThreadId();

    /**
     * Returns a sorted list of posts
     *
     * @return
     */
    public List<Post> getPosts();

    public List<String> getSentiment(Post p);
}
