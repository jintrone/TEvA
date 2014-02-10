package edu.mit.cci.adapters.csv;

import com.csvreader.*;
import edu.mit.cci.teva.model.ConversationImpl;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
        this(cols, corpusname, inputFile, ',', true);
    }

    public CsvBasedConversation(String[] cols, String corpusname, InputStream inputFile, char delim, boolean useTextQual) throws IOException, ParseException {
        super(corpusname);
        CsvBasedConversation.cols = cols;
        CsvReader reader = new CsvReader(new InputStreamReader(inputFile),delim);
        reader.setUseTextQualifier(useTextQual);
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


}
