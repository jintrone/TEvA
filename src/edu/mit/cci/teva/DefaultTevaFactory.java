package edu.mit.cci.teva;

import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.teva.cpm.cfinder.CFinderCommunityFinder;
import edu.mit.cci.teva.engine.*;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.SimilarityBasedAssignment;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.preprocessing.AlphaNumericTokenizer;
import edu.mit.cci.text.preprocessing.DictionaryMunger;
import edu.mit.cci.text.preprocessing.Munger;
import edu.mit.cci.text.preprocessing.StopwordMunger;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.BasicBinningStrategy;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.TimeBasedSlidingWindowStrategy;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.CorpusToNetworkGenerator;
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

    protected TevaParameters params;
    protected Conversation conversation;
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
                mungers.add(StopwordMunger.readAsNew(new FileInputStream(params.getStopwordList())));
                log.info("Loaded stopword list from file: "+params.getStopwordList());
            } else {
                mungers.add(StopwordMunger.readAsNew(getClass().getResourceAsStream(("/" + params.getStopwordList()))));
                log.info("Loaded stopword list from resource: "+params.getStopwordList());
            }
        }
        return mungers.toArray(new Munger[mungers.size()]);
    }

    public Tokenizer<String> getTokenizer() throws IOException {
        return new AlphaNumericTokenizer(getMungers());
    }

    public MergeStrategy getMerger() {
        return new SlowMergeStrategy(getFinder(),params);
    }

    public EvolutionStepStrategy getStepper(CommunityModel model) {
        return new BasicStepStrategy(model, params);
    }


    public WindowStrategy.Factory<Windowable> getTopicWindowingFactory() {
        return new WindowStrategy.Factory<Windowable>() {

            public WindowStrategy<Windowable> getStrategy() {
                return new TimeBasedSlidingWindowStrategy(conversation.getFirstPost().getTime(), conversation.getLastPost().getTime(), params.getWindowSize(), params.getWindowDelta());
            }
        };
    }

    public WindowStrategy.Factory<Windowable> getPostAssignmentWindowingFactory() {
        return new WindowStrategy.Factory<Windowable>() {

            public WindowStrategy<Windowable> getStrategy() {
                return new TimeBasedSlidingWindowStrategy(conversation.getFirstPost().getTime(), conversation.getLastPost().getTime(), params.getMembershipWindowSize(), params.getMembershipWindowDelta());
            }
        };
    }

    public TextToNetworkGenerator getNetworkCalculator() {
        return new LinearWeightNetworkGenerator(params.getWordijIndirection(), params.getWordijTupleSize(), params.getWordijMaxWeight());
    }


    public CorpusToNetworkGenerator<Windowable> getNetworkGenerator(BinningStrategy<Windowable> binningStrategy) {
        return new CorpusToNetworkGenerator<>(binningStrategy,this.getNetworkCalculator(),params.getOverwriteNetworks(), JungUtils.MergePolicy.valueOf(params.getParallelNetworkMergePolicy()));
    }

    @Override
    public CommunityMembershipStrategy getMembershipMatchingStrategy() {
        return new SimilarityBasedAssignment();
    }

    public CommunityFinder getFinder() {
        return new CFinderCommunityFinder(params.getOverwriteNetworks(), params.getOverwriteAnalyses(), params);
    }

    public BinningStrategy<Windowable> getTopicBinningStrategy(List<List<Windowable>> data, WindowStrategy.Factory<Windowable> windows) {
        return new BasicBinningStrategy<>(data, windows);

    }

    public BinningStrategy<Windowable> getMembershipBinningStrategy(List<List<Windowable>> data, WindowStrategy.Factory<Windowable>windows) {
        return new BasicBinningStrategy<>(data, windows);
    }

    @Override
    public TopicMembershipEngine getMembershipEngine(CommunityModel model, Conversation conversation) {
        return new BinningMembershipEngine(model,conversation,this);
    }



}
