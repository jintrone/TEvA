package edu.mit.cci.teva.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 4/22/13
 * Time: 10:55 AM
 */
public class MockPost implements Post {

    public static int id = 0;

    public String postid;

    public String threadid;

    public String replyid;

    public Date start;

    public MockPost(String threadid, Post reply, Date date) {
        postid = "MockPost."+(id++);
        this.threadid = threadid;
        this.replyid = reply!=null?reply.getPostid():null;
        this.start = date;

    }

    public String getThreadid() {
        return threadid;
    }

    public String getPostid() {
       return postid;
    }

    public String getReplyToId() {
        return replyid;
    }

    public String getUserid() {
        return "DummyUser";
    }

    public Date getTime() {
     return start;
    }

    public List<String> getSentiment() {
        return Collections.emptyList();
    }

    public String getContent() {
        return "This is a dummy post.";
    }

    public String toString() {
        return "<post id="+getPostid()+" date="+getTime()+" replyTo="+getReplyToId()+"><content>"+getContent()+"</content></post>";
    }
}
