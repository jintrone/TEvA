package edu.mit.cci.text.wordij;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.util.ThreadPoolRunner;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by josh on 7/16/14.
 */
public class ParallelCorpusToNetworkGenerator<T extends Windowable> extends CorpusToNetworkGenerator<T> {


    private ThreadPoolRunner pool = new ThreadPoolRunner(8);

    public ParallelCorpusToNetworkGenerator(BinningStrategy bmodel, TextToNetworkGenerator calculator) {
        super(bmodel, calculator);
    }

    public ParallelCorpusToNetworkGenerator(BinningStrategy<T> bmodel,
                                    TextToNetworkGenerator calculator, boolean overwrite, JungUtils.MergePolicy merger) {
       super(bmodel,calculator,overwrite,merger);

    }












}
