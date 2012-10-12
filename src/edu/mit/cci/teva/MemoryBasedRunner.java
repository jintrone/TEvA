package edu.mit.cci.teva;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.cpm.cfinder.CFinderCommunityFinder;
import edu.mit.cci.teva.engine.*;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.TevaUtils;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.preprocessing.AlphaNumericTokenizer;
import edu.mit.cci.text.preprocessing.DictionaryMunger;
import edu.mit.cci.text.preprocessing.Munger;
import edu.mit.cci.text.preprocessing.StopwordMunger;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.BasicBinningStrategy;
import edu.mit.cci.text.windowing.TimeBasedSlidingWindowStrategy;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.CorpusToNetworkGenerator;
import edu.mit.cci.text.wordij.LinearWeightNetworkGenerator;
import edu.mit.cci.text.wordij.TextToNetworkGenerator;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/3/12
 * Time: 9:39 PM
 */
public class MemoryBasedRunner {


    Logger log = Logger.getLogger(MemoryBasedRunner.class);
    TevaParameters params;
    Conversation conversation;
    private WindowStrategy.Factory<Windowable> windowStrategyFactory;


    public MemoryBasedRunner(Conversation c) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters(this.getClass().getResourceAsStream("/teva.properties"));
        process();
    }

    public MemoryBasedRunner(Conversation c, InputStream parameters) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters(parameters);
        process();
    }

    public NetworkProvider getNetworkProvider() throws IOException {

        NetworkProvider provider;
        if (params.getSkipNetworkGeneration()) {
            WindowStrategy.Factory<Windowable> factory = getWindowStrategyFactory();
            final int i = factory.getStrategy().getNumberWindows();
            provider = new NetworkProvider() {
                public int getNumberWindows() {
                    return i;
                }

                public Network getNetworkAt(int i) {
                    File f = CFinderCommunityFinder.getNetworkFile(new File(params.getWorkingDirectory()),i,params.getFilenameIdentifier());
                    try {
                        return NetworkUtils.readNetworkFile(f);
                    } catch (IOException e) {
                        log.error("Error reading network file "+f.getAbsolutePath()+", cannot proceed");
                        throw new RuntimeException(e);
                    }
                }
            };
        } else {
            BasicBinningStrategy<Windowable> binningStrategy = new BasicBinningStrategy<Windowable>(getConversationData(), getWindowStrategyFactory());
            CorpusToNetworkGenerator<Windowable> networkGenerator = new CorpusToNetworkGenerator<Windowable>(binningStrategy, getNetworkCalculator());
            final List<Network> result = networkGenerator.analyze();
            log.info("Done creating networks.");
            provider = new NetworkProvider() {
                public int getNumberWindows() {
                    return result.size();
                }

                public Network getNetworkAt(int i) {
                    return result.get(i);
                }
            };
        }

        return provider;
    }

    public void process() throws IOException, CommunityFinderException, JAXBException {
        log.info("Begin process.");
        NetworkProvider provider = getNetworkProvider();
        CommunityModel model = new CommunityModel();
        EvolutionEngine engine = new EvolutionEngine(model,params, provider, getFinder(), getStepper(model), getMerger());
        engine.process();
        model.setWindows(getWindowStrategyFactory().getStrategy().getWindowBoundaries());
        TevaUtils.serialize(new File("CommunityOutput."+conversation.getName()+"."+params.getFilenameIdentifier()+".xml"), model, CommunityModel.class);

    }

    private MergeStrategy getMerger() {
        return new FastMergeStrategy(params.getFixedCliqueSize());
    }

    private EvolutionStepStrategy getStepper(CommunityModel model) {
        return new BasicStepStrategy(model, params);
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


    public Tokenizer<String> getTokenizer() throws IOException {
        List<Munger> mungers = new ArrayList<Munger>();
        if (params.getReplacementDictionary() != null) {
            mungers.add(DictionaryMunger.read(getClass().getResourceAsStream("/" + params.getReplacementDictionary())));
        }
        if (params.getStopwordList() != null) {
            mungers.add(StopwordMunger.read(getClass().getResourceAsStream(("/" + params.getStopwordList()))));
        }

        return new AlphaNumericTokenizer(mungers.toArray(new Munger[0]));
    }


    public WindowStrategy.Factory<Windowable> getWindowStrategyFactory() {
        return new WindowStrategy.Factory<Windowable>() {

            public WindowStrategy<Windowable> getStrategy() {
                return new TimeBasedSlidingWindowStrategy<Windowable>(conversation.getFirstPost().getTime(), conversation.getLastPost().getTime(), params.getWindowSize(), params.getWindowDelta());
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
