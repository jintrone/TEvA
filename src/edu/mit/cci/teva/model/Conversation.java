package edu.mit.cci.teva.model;

import java.util.List;

/**
 * User: jintrone
 * Date: 8/11/11
 * Time: 11:06 AM
 */
public interface Conversation {

    public String getName();
    public Post getFirstPost();
    public Post getLastPost();
    public List<DiscussionThread> getAllThreads();
    public List<String> getUsers();

}
