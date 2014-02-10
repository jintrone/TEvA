package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;
import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.TextToNetworkGenerator;

import java.io.IOException;
import java.util.*;

/**
 * Created by josh on 1/14/14.
 */
public class NumTokensMembershipEngine implements TopicMembershipEngine {


    private final TevaFactory factory;
    private final Conversation conversation;
    private final CommunityModel model;
    private Map<String, PostTokens> postmap;
    private Tokenizer<String> tokenizer;
    private TextToNetworkGenerator generator;

    private Map<String, String> communityAssignmentMap;

    private CommunityMembershipStrategy strategy;

    private int numTokens = 1;

    public NumTokensMembershipEngine(CommunityModel model, Conversation conversation,  TevaFactory factory, int numTokens) {
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
        this.numTokens = numTokens;
        strategy = factory.getMembershipMatchingStrategy();
    }



    public void process() throws IOException {
        communityAssignmentMap = new HashMap<String, String>();
        Date[][] windows = model.getWindows();
        List<Date> ends = new ArrayList<Date>();
        postmap = new HashMap<String, PostTokens>();
        for (Date[] w : windows) {
            ends.add(w[1]);
        }

        for (DiscussionThread t : conversation.getAllThreads()) {
            for (Post p : t.getPosts()) {
                postmap.put(p.getPostid(), new PostTokens(p,tokenizer.tokenize(p.getContent())));
                int win = Collections.binarySearch(ends, p.getTime());
                if (win < 0) {
                    win = -(win + 1);
                }

                findCommunityInWindows(p, win);
            }
        }

    }

    public List<String> getTokens(Post p) {
        List<String> tokens = new ArrayList<String>();
        PostTokens pt = postmap.get(p.getPostid());
        while (tokens.size()<numTokens && pt!=null) {
            if (pt.tokens!=null) {tokens.addAll(pt.tokens);}
            pt = postmap.get(pt.post.getReplyToId());
        }
        return tokens;
    }

    public void findCommunityInWindows(Post p, int win) {

        List<String> tokens =  getTokens(p);
        Network net = generator.calculateWeights(null, tokens);
        List<CommunityMembershipStrategy.Assignment> assignments = strategy.assignToCommunity(model,win,net);
        for (CommunityMembershipStrategy.Assignment a: assignments) {
            a.community.addAssignment(new ConversationChunk(Collections.singletonList((Windowable)new WindowablePostAdapter(p,tokenizer)), win, a.coverage, a.similarity,a.edges));
        }

    }

    private class PostTokens {
        Post post;
        List<String> tokens;

        PostTokens(Post p, List<String> tokens) {
            this.post = p;
            this.tokens = tokens;
        }


    }
}
