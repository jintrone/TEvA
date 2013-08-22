package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;

import java.util.Collection;
import java.util.List;

/**
 * User: jintrone
 * Date: 5/5/13
 * Time: 10:21 PM
 */
public class SlowMergeStrategy implements MergeStrategy {

    private CommunityFinder finder;
    private TevaParameters params;

    public SlowMergeStrategy(CommunityFinder finder, TevaParameters params) {
        this.finder = finder;
        this.params = params;
    }

    public List<Network> process(Network from, Collection<CommunityFrame> cdfrom, Network to, Collection<CommunityFrame> cdto, int window) {
        Network combined = NetworkUtils.combine(from,to);
        for (Network n:cdfrom) {
            combined = NetworkUtils.combine(combined,n);
        }
        List<? extends Network> result;
        try {
            result = finder.findCommunities(combined,params.getFixedCliqueSize(),window,"combined");
        } catch (CommunityFinderException e) {
            throw new RuntimeException("Error processing communities in merged graph",e);
        }
        return (List<Network>) result;

    }
}
