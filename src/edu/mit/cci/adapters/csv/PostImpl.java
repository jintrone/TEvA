package edu.mit.cci.adapters.csv;

import edu.mit.cci.teva.model.Post;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
* Created by josh on 1/20/14.
*/
class PostImpl implements Post {

    public String postId,replyToId,threadId,userId,content;
    public Date creation;



    public PostImpl(String postId, String replyToId, String userId, Date creation) {

        this.postId = postId;
        this.replyToId = replyToId;
        this.userId = userId;
        this.creation = creation;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setReplyToId(String replyToId) {
        this.replyToId = replyToId;
    }


    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThreadid() {
        return threadId;
    }

    public String getPostid() {
        return postId;
    }

    public String getReplyToId() {
       return replyToId;
    }

    public String getUserid() {
       return userId;
    }

    public Date getTime() {
        return creation;
    }

    public List<String> getSentiment() {
        return Collections.emptyList();
    }

    public String getContent() {
        return content;
    }
}
