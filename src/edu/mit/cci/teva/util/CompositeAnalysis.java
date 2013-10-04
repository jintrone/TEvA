package edu.mit.cci.teva.util;

import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.engine.CommunityMembershipStrategy;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.TextToNetworkGenerator;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 8/30/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompositeAnalysis {

    private final CommunityModel topic;
    private final CommunityModel social;
    private TevaFactory factory;
    private CommunityMembershipStrategy assignmentStrategy = new ExhaustiveAssignment();

    public CompositeAnalysis(TevaFactory topicFactory,CommunityModel social, CommunityModel topic) {
        this.social = social;
        this.topic = topic;
        this.factory = topicFactory;
        if (social.getWindows().length != topic.getWindows().length) {
            throw new RuntimeException("Window lengths must be identical");
        }
    }

    public void analyze() throws IOException {
        List<List<Windowable>> data = factory.getConversationData();
        BinningStrategy<Windowable> binningStrategy = factory.getTopicBinningStrategy(factory.getConversationData(), factory.getTopicWindowingFactory());
        Tokenizer<String> tokenizer = factory.getTokenizer();
        TextToNetworkGenerator generator = factory.getNetworkCalculator();

    }
}
