package edu.mit.cci.text.preprocessing;

/**
 * User: jintrone
 * Date: 5/11/11
 * Time: 5:11 PM
 */


import java.util.List;

/**
 * Generic interface for manipulating words from a data set - for handling things like stemming and stop lists
 * Not appropriate for block level operations (e.g. removing quoted sections)
 */
public interface Munger {




    /**
     *
     * @param from
     * @return true if there is new data in the buffer, false otherwise
     */
    public boolean read(String from);

    /**
     *
     * @return Returns all data and clears the buffer
     */
    public List<String> flush();

    /**
     * Finish processing any remaining state and return result
     * Clients should be sure to call this after processing is finished
     */
    public List<String> finish();






}
