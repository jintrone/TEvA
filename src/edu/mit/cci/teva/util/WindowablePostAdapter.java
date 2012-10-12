package edu.mit.cci.teva.util;

import edu.mit.cci.teva.model.Post;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.Windowable;

import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/4/12
 * Time: 4:30 PM
 */
public class WindowablePostAdapter implements Windowable {

    private Post post;
    private Tokenizer<String> tokenizer;

    public WindowablePostAdapter(Post p, Tokenizer<String> tokenizer) {
        this.post = p;
        this.tokenizer = tokenizer;
    }

    public Date getStart() {
        return post.getTime();
    }

    public List<String> getTokens() {
       return tokenizer.tokenize(post.getContent());
    }
}
