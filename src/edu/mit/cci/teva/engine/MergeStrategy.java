package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;

import java.util.Collection;
import java.util.List;

/**
 * User: jintrone
 * Date: 7/25/12
 * Time: 5:14 PM
 */
public interface MergeStrategy {
    List<Network> process(Network from, Collection<CommunityFrame> cdfrom, Network to, Collection<CommunityFrame> cdto, int window);
}
