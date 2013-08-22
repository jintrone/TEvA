package edu.mit.cci.adapters.csv;

import com.Ostermiller.util.ExcelCSVParser;
import com.csvreader.*;
import com.sun.tools.corba.se.idl.StringGen;
import edu.mit.cci.teva.model.ConversationImpl;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;


import javax.security.sasl.AuthorizeCallback;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 12/12/12
 * Time: 3:25 PM
 */
public class CsvBasedConversation extends ConversationImpl {

    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String[] cols = {"id","replyTo","created","author","text"};

    public static enum Column {
        ID(0), REPLY(1), CREATED(2), AUTHOR(3), TEXT(4);

        private String colname;

        Column(int i) {
            this.colname = cols[i];
        }

        public String getColumnName() {
            return colname;
        }
    }

    public CsvBasedConversation(String corpusname, InputStream inputFile) throws IOException, ParseException {
        this(cols, corpusname, inputFile);
    }

    public CsvBasedConversation(String[] cols, String corpusname, InputStream inputFile) throws IOException, ParseException {
        super(corpusname);
        CsvBasedConversation.cols = cols;
        CsvReader reader = new CsvReader(new InputStreamReader(inputFile));
        if (!reader.readHeaders()) {
            throw new RuntimeException("Empty input file?");
        };
        for (Column c:Column.values()) {
            if (reader.getIndex(c.getColumnName())<0) {
                throw new RuntimeException("Could not locate header: "+c.getColumnName());
            }
        }
        Map<Post,DiscussionThread> threadMap = new HashMap<Post, DiscussionThread>();
        Map<String,Post> postMap = new HashMap<String, Post>();

        while (reader.readRecord()) {



            if (skip(reader)) continue;
            PostImpl p = new PostImpl(processString(Column.ID, reader),processString(Column.REPLY, reader),
                    processString(Column.AUTHOR, reader),processDate(Column.CREATED,reader));
            p.setContent(reader.get(Column.TEXT.getColumnName()));
            postMap.put(p.getPostid(),p);

            if (p.getReplyToId()==null || p.getReplyToId().isEmpty()) {
                DiscussionThreadImpl d = new DiscussionThreadImpl("thread-"+p.getPostid());
                d.addPost(p);
                threadMap.put(p,d);
            }

        }

        Map<Post,Set<Post>> childmap = new HashMap<Post, Set<Post>>();
        for (Post p:postMap.values()) {
            if (threadMap.containsKey(p)) continue;
            Post parent = postMap.get(p.getReplyToId());
            Set<Post> children = childmap.get(parent);
            if (children == null) {
                children = new HashSet<Post>();
                childmap.put(parent,children);
            }
            children.add(p);
        }

        for (Map.Entry<Post,DiscussionThread> ent:threadMap.entrySet()) {
            addChildren(ent.getKey(), (DiscussionThreadImpl) ent.getValue(),childmap);
        }

        for (DiscussionThread thread:threadMap.values()) {
            this.addThread(thread);
        }


    }

    public boolean skip(CsvReader reader) throws IOException {
        return false;
    }

    public String processString(Column field, CsvReader reader) throws IOException {
        return (String)reader.get(field.getColumnName());
    }

    public Date processDate(Column field, CsvReader reader) throws ParseException, IOException {
        return format.parse(processString(field,reader));
    }


    public void addChildren(Post top, DiscussionThreadImpl thread, Map<Post,Set<Post>> childmap) {
        if (!childmap.containsKey(top)) return;
        Set<Post> children = childmap.get(top);
        for (Post child:children) {
            thread.addPost(child);
            addChildren(child, thread, childmap);
        }
    }



    private static class PostImpl implements Post {

        public String postId,replyToId,threadId,userId,content;
        public Date creation;



        public PostImpl(String postId,String replyToId, String userId, Date creation) {

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


    public static class DiscussionThreadImpl implements DiscussionThread {

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
                Collections.sort(posts,new Comparator<Post>() {
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
}
