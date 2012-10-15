package edu.mit.cci.teva.engine;


import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.SingleThreadBinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.CorpusToNetworkGenerator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 6/20/11
 * Time: 9:37 PM
 */
public class TopicMembershipEngine {


    Conversation info;


    CommunityModel communities;
    private TevaFactory factory;

    private Map<Integer, Integer> activeThreads = new HashMap<Integer, Integer>();

    private static Logger log = Logger.getLogger(TopicMembershipEngine.class);

    public TopicMembershipEngine(CommunityModel communities, Conversation info, TevaFactory factory) {
        this.info = info;


        this.communities = communities;
        this.factory = factory;

    }

    public void process() throws IOException {
        log.debug("Running topic membership assignment");
        List<List<Windowable>> data = factory.getConversationData();
        for (List<Windowable> thread : data) {
            BinningStrategy<Windowable> binningStrategy = new SingleThreadBinningStrategy(thread, factory.getPostAssignmentWindowingFactory());
            CorpusToNetworkGenerator<Windowable> networkGenerator = new CorpusToNetworkGenerator<Windowable>(binningStrategy, factory.getNetworkCalculator());
            //updateThreadStats(wstrategy);
            List<Network> nets = networkGenerator.analyze();
            updateCommunities(nets, binningStrategy);
        }
        log.debug("Done topic membership assignment");
    }


    public void updateCommunities(List<Network> nets, BinningStrategy<Windowable> strategy) {

        for (int i = 0; i < communities.getWindows().length; i++) {
            CommunityScore best = null;
            for (Community c : communities.getCommunities()) {
                float coverage = NetworkUtils.coverage(nets.get(i), c.getCommunityAtBin(i));
                float similarity = NetworkUtils.similarity(nets.get(i), c.getCommunityAtBin(i));

                //TODO handle the unlikely possibility there could be more than one match here.
                if (coverage > 0) {
                    CommunityScore cs = new CommunityScore(i, c, coverage, similarity);
                    if (best == null) {
                        best = cs;
                    } else if (best.coverage < cs.coverage || best.coverage == cs.coverage && best.similarity < cs.coverage) {
                        best = cs;
                    }
                }


            }
            if (best == null) {
                log.info("Could not identify topic for posts");
            } else {
                List<Windowable> data = strategy.getDataAtWindow(best.win).get(0);
                best.community.addAssignment(new ConversationChunk(data,i, best.coverage, best.similarity));
            }

        }
    }

    public static void main(String[] args) {

    }


}
