package edu.mit.cci.text.preprocessing;

import java.util.List;

/**
 * Most clients will use the tokenizer to split a data stream into tokens.  The tokenizer insulates the client
 * from details about the underlying text transformation routines.
 *
 * User: jintrone
 * Date: 9/21/12
 * Time: 5:00 PM
 */
public interface Tokenizer<T> {


    public List<String> tokenize(T text);
}
