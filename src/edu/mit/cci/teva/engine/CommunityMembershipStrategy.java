package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.text.windowing.Bin;
import edu.mit.cci.text.windowing.Windowable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 8/30/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CommunityMembershipStrategy {

    public List<Assignment> assignToCommunity(CommunityModel communities, int window, Network net);

    public static class Assignment {

        public float coverage;
        public float similarity;
        public Community community;
        public Collection<Edge> edges;

        public Assignment(Community c, float coverage, float similarity) {
            this.coverage = coverage;
            this.similarity = similarity;
            this.community = c;
        }

        public Assignment(Community c, float coverage, float similarity, Collection<Edge> edges) {
           this(c,coverage,similarity);
            this.edges = edges;
        }

    }


}
