package edu.mit.cci.sna;

/**
 * User: jintrone
 * Date: 9/21/12
 * Time: 8:54 PM
 */
public interface NetworkFactory {

    public Edge addEdge(String from, String to, boolean directed);
    public Network getNetwork();
}
