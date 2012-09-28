package edu.mit.cci.text.wordij;

import edu.mit.cci.sna.Network;


import java.util.List;

/**
 * Generates a network based on adjacency relationships in an underlying sequential data frame.
 *
 * Consider a network constructed by placing a link between all adjacent tokens in an underlying
 * data frame. Let us define the degree of indirection between any two nodes as the unweighted
 * length of the shortest path between these nodes minus 1.
 *
 * For example, in the sequence "A B C", "A" and "B" have a degree of indirection = 0, and
 * "A" and "C" have a degree of indirection =1.
 *
 * It is assumed that implementers will add links to the network and weight them
 * according to degree of indirection as defined above.
 *
 * User: jintrone
 * Date: 5/11/11
 * Time: 5:32 PM
 */
public interface TextToNetworkGenerator {

    public Network calculateWeights(List<String> tokens);
}
