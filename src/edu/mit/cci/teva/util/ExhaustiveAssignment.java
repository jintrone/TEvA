package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.engine.*;
import edu.mit.cci.text.windowing.Bin;
import edu.mit.cci.text.windowing.Windowable;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 8/30/13
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExhaustiveAssignment implements CommunityMembershipStrategy {

    Logger log = Logger.getLogger(ExhaustiveAssignment.class);

    @Override
    public List<Assignment> assignToCommunity(CommunityModel communities, int window, Network net) {
        List<Assignment> scores = new ArrayList<>();
        for (Community c : communities.getCommunities()) {
            EdgesCoverage e = coverage(net, c.getCommunityAtBin(window));
            float similarity = NetworkUtils.similarity(net, c.getCommunityAtBin(window));


            //TODO handle the unlikely possibility there could be more than one match here.

            if (e.coverage > 0) {
                scores.add(new Assignment(c, e.coverage, similarity,e.edges));
            }
        }

        return scores;
    }

    private  EdgesCoverage coverage(Network one, Network two) {
        if (one == null || two == null || one.getNodes().isEmpty() || two.getNodes().isEmpty()) {
            return new EdgesCoverage(Collections.<Edge>emptySet(),0f);
        } else {
            Set<Edge> fromedges = new HashSet<Edge>(one.getEdges());
            float denom = fromedges.size();
            fromedges.retainAll(two.getEdges());

            return new EdgesCoverage(fromedges,(float) fromedges.size() / denom);
        }

    }

    private class EdgesCoverage {

        private final float coverage;
        private final Collection<Edge> edges;

        EdgesCoverage(Collection<Edge> edges, float coverage) {
            this.edges = edges;
            this.coverage = coverage;
        }
    }


}
