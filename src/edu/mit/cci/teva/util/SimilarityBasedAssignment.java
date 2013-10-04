package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.engine.*;
import edu.mit.cci.text.windowing.Bin;
import edu.mit.cci.text.windowing.Windowable;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<Community,List<ConversationChunk>> assignToCommunity(CommunityModel communities, int window, Network net, Bin<Windowable> bin) {
        Map<Community, List<ConversationChunk>> result = new HashMap<Community,List<ConversationChunk>>();
        CommunityScore best = null;
        for (Community c : communities.getCommunities()) {
            float coverage = NetworkUtils.coverage(net, c.getCommunityAtBin(window));
            float similarity = NetworkUtils.similarity(net, c.getCommunityAtBin(window));

            //TODO handle the unlikely possibility there could be more than one match here.
            if (coverage > 0) {
                CommunityScore cs = new CommunityScore(window, c, coverage, similarity);
                if (best == null) {
                    best = cs;
                } else if (best.coverage < cs.coverage || best.coverage == cs.coverage && best.similarity < cs.similarity) {
                    best = cs;
                }
            }


        }
        if (best == null) {
            log.info("Could not identify topic for posts");
        } else {
            if (!result.containsKey(best.community)) {
                result.put(best.community,new ArrayList<ConversationChunk>());
            }

            result.get(best.community).add(new ConversationChunk(bin.subList(bin.getFirstItemIndex(),bin.size()), window, best.coverage, best.similarity));
        }
        return result;
    }
}
