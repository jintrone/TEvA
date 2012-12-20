package edu.mit.cci.teva;

import edu.mit.cci.teva.cpm.cfinder.CFinderCommunityFinder;
import edu.mit.cci.teva.engine.BasicStepStrategy;
import edu.mit.cci.teva.engine.CommunityFinder;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.engine.EvolutionStepStrategy;
import edu.mit.cci.teva.engine.FastMergeStrategy;
import edu.mit.cci.teva.engine.MergeStrategy;
import edu.mit.cci.teva.engine.TevaParameters;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.preprocessing.AlphaNumericTokenizer;
import edu.mit.cci.text.preprocessing.DictionaryMunger;
import edu.mit.cci.text.preprocessing.Munger;
import edu.mit.cci.text.preprocessing.StopwordMunger;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.TimeBasedSlidingWindowStrategy;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.LinearWeightNetworkGenerator;
import edu.mit.cci.text.wordij.TextToNetworkGenerator;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Intended to allow pluggable implementations of modules.  Perhaps look to Spring instead, though?
 * <p/>
 * User: jintrone
 * Date: 10/8/12
 * Time: 1:05 PM
 */
public class DefaultTevaFactory implements TevaFactory {

    private TevaParameters params;
    private Conversation conversation;
    private static Logger log = Logger.getLogger(DefaultTevaFactory.class);

    public DefaultTevaFactory(TevaParameters params, Conversation conversation) {
        this.params = params;
        this.conversation = conversation;

    }

    public List<List<Windowable>> getConversationData() throws IOException {
        Tokenizer<String> tokenizer = getTokenizer();
        List<List<Windowable>> data = new ArrayList<List<Windowable>>();
        for (DiscussionThread thread : conversation.getAllThreads()) {
            List<Windowable> threaddata = new ArrayList<Windowable>();
            for (Post p : thread.getPosts()) {
                threaddata.add(new WindowablePostAdapter(p, tokenizer));
            }
            data.add(threaddata);
        }
        return data;
    }

    public Munger[] getMungers() throws IOException {
         List<Munger> mungers = new ArrayList<Munger>();
        if (params.getReplacementDictionary() != null && !params.getReplacementDictionary().isEmpty()) {
            if (params.getReplacementDictionary().startsWith("/") || params.getReplacementDictionary().startsWith(".")) {
                mungers.add(DictionaryMunger.read(new FileInputStream(params.getReplacementDictionary())));
                log.info("Loaded replacement list from file: "+params.getReplacementDictionary());
            } else {
                mungers.add(DictionaryMunger.read(getClass().getResourceAsStream("/" + params.getReplacementDictionary())));
                log.info("Loaded replacement list from resource: "+params.getReplacementDictionary());
            }
        }
        if (params.getStopwordList() != null && !params.getStopwordList().isEmpty()) {
            if (params.getReplacementDictionary().startsWith("/") || params.getReplacementDictionary().startsWith(".")) {
                mungers.add(StopwordMunger.read(new FileInputStream(params.getStopwordList())));
                log.info("Loaded stopword list from file: "+params.getStopwordList());
            } else {
                mungers.add(StopwordMunger.read(getClass().getResourceAsStream(("/" + params.getStopwordList()))));
                log.info("Loaded stopword list from resource: "+params.getStopwordList());
            }
        }
        return mungers.toArray(new Munger[mungers.size()]);
    }

    public Tokenizer<String> getTokenizer() throws IOException {
        return new AlphaNumericTokenizer(getMungers());
    }

    public MergeStrategy getMerger() {
        return new FastMergeStrategy(params.getFixedCliqueSize());
    }

    public EvolutionStepStrategy getStepper(CommunityModel model) {
        return new BasicStepStrategy(model, params);
    }


    public WindowStrategy.Factory<Windowable> getTopicWindowingFactory() {
        return new WindowStrategy.Factory<Windowable>() {

            public WindowStrategy<Windowable> getStrategy() {
                return new TimeBasedSlidingWindowStrategy<Windowable>(conversation.getFirstPost().getTime(), conversation.getLastPost().getTime(), params.getWindowSize(), params.getWindowDelta());
            }
        };
    }

    public WindowStrategy.Factory<Windowable> getPostAssignmentWindowingFactory() {
        return new WindowStrategy.Factory<Windowable>() {

            public WindowStrategy<Windowable> getStrategy() {
                return new TimeBasedSlidingWindowStrategy<Windowable>(conversation.getFirstPost().getTime(), conversation.getLastPost().getTime(), params.getMembershipWindowSize(), params.getMembershipWindowDelta());
            }
        };
    }

    public TextToNetworkGenerator getNetworkCalculator() {
        return new LinearWeightNetworkGenerator(params.getWordijIndirection(), params.getWordijTupleSize());
    }

    public CommunityFinder getFinder() {
        return new CFinderCommunityFinder(params.getOverwriteNetworks(), params.getOverwriteAnalyses(), params);
    }


}
