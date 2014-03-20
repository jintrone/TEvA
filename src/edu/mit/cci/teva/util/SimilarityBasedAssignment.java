package edu.mit.cci.teva.util;

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
public class SimilarityBasedAssignment implements CommunityMembershipStrategy {

    Logger log = Logger.getLogger(SimilarityBasedAssignment.class);

    @Override
    public List<Assignment> assignToCommunity(CommunityModel communities, int window, Network net) {
        Assignment best = null;
        for (Community c : communities.getCommunities()) {
            float coverage = NetworkUtils.coverage(net, c.getCommunityAtBin(window));
            float similarity = NetworkUtils.similarity(net, c.getCommunityAtBin(window));

            //TODO handle the unlikely possibility there could be more than one match here.
            if (coverage > 0) {

                if (best == null) {
                    best = new Assignment(c, coverage, similarity);
                } else if (best.coverage < coverage || best.coverage == coverage && best.similarity < similarity) {
                    best.community = c;
                    best.coverage = coverage;
                    best.similarity = similarity;
                }
            }


        }
        if (best == null) {
            log.info("Could not identify topic for posts");
            return Collections.emptyList();
        } else {
            return Collections.singletonList(best);


        }

    }
}
