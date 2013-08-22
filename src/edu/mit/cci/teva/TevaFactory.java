package edu.mit.cci.teva;

import com.sun.java.swing.plaf.windows.resources.windows;
import edu.mit.cci.teva.engine.CommunityFinder;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.engine.EvolutionStepStrategy;
import edu.mit.cci.teva.engine.MergeStrategy;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.CorpusToNetworkGenerator;
import edu.mit.cci.text.wordij.TextToNetworkGenerator;

import java.io.IOException;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/12/12
 * Time: 11:22 PM
 */
public interface TevaFactory {
    public List<List<Windowable>> getConversationData() throws IOException;

    public Tokenizer<String> getTokenizer() throws IOException;

    public MergeStrategy getMerger();

    public EvolutionStepStrategy getStepper(CommunityModel model);

    public WindowStrategy.Factory<Windowable> getTopicWindowingFactory();

    public WindowStrategy.Factory<Windowable> getPostAssignmentWindowingFactory();

    public TextToNetworkGenerator getNetworkCalculator();

    public CommunityFinder getFinder();

    public BinningStrategy<Windowable> getTopicBinningStrategy(List<List<Windowable>> data, WindowStrategy.Factory<Windowable> windows);

    public BinningStrategy<Windowable> getMembershipBinningStrategy(List<List<Windowable>> data, WindowStrategy.Factory<Windowable>windows);

    CorpusToNetworkGenerator<Windowable> getNetworkGenerator(BinningStrategy<Windowable> binningStrategy);
}
