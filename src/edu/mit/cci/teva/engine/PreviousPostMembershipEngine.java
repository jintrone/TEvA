package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.wordij.TextToNetworkGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 5/9/13
 * Time: 11:22 PM
 */
public class PreviousPostMembershipEngine implements TopicMembershipEngine {


    private final TevaFactory factory;
    private final Conversation conversation;
    private final CommunityModel model;
    private Map<String, Post> postmap;
    private Tokenizer<String> tokenizer;
    private TextToNetworkGenerator generator;

    private Map<String, String> communityAssignmentMap;

    public PreviousPostMembershipEngine(CommunityModel model, Conversation conversation,  TevaFactory factory) {
        this.factory = factory;
        this.conversation = conversation;
        this.model = model;
        try {
            tokenizer = factory.getTokenizer();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException(e);
        }
        generator = factory.getNetworkCalculator();

    }

    public Map<String,String> getPostAssignment() {
        return communityAssignmentMap;
    }

    public void process() throws IOException {
        communityAssignmentMap = new HashMap<String, String>();
        Date[][] windows = model.getWindows();
        List<Date> ends = new ArrayList<Date>();
        postmap = new HashMap<String, Post>();
        for (Date[] w : windows) {
            ends.add(w[1]);
        }

        for (DiscussionThread t : conversation.getAllThreads()) {
            for (Post p : t.getPosts()) {
                postmap.put(p.getPostid(), p);
                int winstart = Collections.binarySearch(ends, p.getTime());
                if (winstart < 0) {
                    winstart = -(winstart + 1);
                }
                int winend = winstart;
                while (winend < ends.size() && windows[winend][0].before(p.getTime())) winend++;
                findCommunityInWindows(p, winstart, winend);
            }
        }

    }

    public void findCommunityInWindows(Post p, int winstart, int winend) {
        Post prior = postmap.get(p.getReplyToId());
        List<String> tokens =  new ArrayList<String>();
        if (prior!=null) tokens.addAll(tokenizer.tokenize(prior.getContent()));
        tokens.addAll(tokenizer.tokenize(p.getContent()));

        Network net = generator.calculateWeights(null, tokens);
        CommunityScore best = null;
        for (Community c : model.getCommunities()) {
            for (int window = winstart; window < winend; window++) {
                CommunityFrame f = c.getCommunityAtBin(window);
                if (f == null) continue;
                float coverage = NetworkUtils.coverage(net, f);
                float similarity = NetworkUtils.similarity(net, f);

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
        }

        if (best !=null) {
            communityAssignmentMap.put(p.getPostid(),best.community.getId());
        }


    }
}
