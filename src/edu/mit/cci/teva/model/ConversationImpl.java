package edu.mit.cci.teva.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/29/11
 * Time: 8:44 AM
 */
public class ConversationImpl implements Conversation {

    List<DiscussionThread> threads = new ArrayList<DiscussionThread>();
    Post first = null;
    Post last = null;
    List<String> users = new ArrayList<String>();
    String name = null;

    public ConversationImpl(String name) {
        this.name = name;
    }

    public ConversationImpl(String name, List<? extends DiscussionThread> threads) {
        this.name = name;
        for (DiscussionThread t:threads) {
          addThread(t);
       }
    }


    public void addThread(DiscussionThread t) {
        int insertion = Collections.binarySearch(threads,t,new Comparator<DiscussionThread>() {
            public int compare(DiscussionThread discussionThread, DiscussionThread discussionThread1) {
                return discussionThread.getPosts().get(0).getTime().compareTo(discussionThread1.getPosts().get(0).getTime());
            }
        });
        if (insertion<0) {
            int idx = -insertion-1;
            if (idx == 0 ) {
                first = t.getPosts().get(0);
            }
            Post tlast =  t.getPosts().get(t.getPosts().size()-1);
            if (last == null || tlast.getTime().after(last.getTime())) {
                last = tlast;
            }
            threads.add(idx, t);

        }

        for (Post p:t.getPosts()) {
            if (!users.contains(p.getUserid())) users.add(p.getUserid());
        }


    }


    public String getName() {
        return name;
    }

    public Post getFirstPost() {
        return first;
    }

    public Post getLastPost() {
       return last;
    }

    public List<DiscussionThread> getAllThreads() {
        return threads;
    }

    public List<String> getUsers() {
        return users;
    }
}
