package edu.mit.cci.text.wordij;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.util.NetWriterStrategy;
import edu.mit.cci.text.windowing.Bin;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.uci.ics.jung.graph.Graph;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jintrone
 * Date: 5/11/11
 * Time: 5:07 PM
 */

/**
 * Generates a list of graphs for a corpus, using a windowing strategy to
 * segment an underlying data stream.
 *
 * @param <T>
 */
public class CorpusToNetworkGenerator<T extends Windowable> {

    private final JungUtils.MergePolicy merger;
    BinningStrategy<T> bmodel;
    TextToNetworkGenerator calculator;

    boolean overwrite = true;

    Logger log = Logger.getLogger(CorpusToNetworkGenerator.class);


    public CorpusToNetworkGenerator(BinningStrategy<T> bmodel,
                                    TextToNetworkGenerator calculator) {
        this(bmodel, calculator, true, JungUtils.MergePolicy.ADD_MAX_1);

    }

    public CorpusToNetworkGenerator(BinningStrategy<T> bmodel,
                                    TextToNetworkGenerator calculator, boolean overwrite, JungUtils.MergePolicy merger) {
        this.bmodel = bmodel;
        this.calculator = calculator;
        this.overwrite = overwrite;
        this.merger = merger;

    }

    public List<File> analyzeToFiles(NetWriterStrategy strategy) {
        List<File> results = new ArrayList<File>();
        log.info("Will process " + bmodel.getNumWindows() + " windows");
        for (int win = 0; win < bmodel.getNumWindows(); win++) {
            File f = strategy.getNetworkFilename(win);
            if (!overwrite && f.exists()) {
                log.info("File "+f+" exists, not generating");
                continue;
            }
            log.debug("Process window " + win);
            try {
                results.add(strategy.writeNetwork(analyzeWindow(win), win));
            } catch (Exception e) {
                throw new RuntimeException("Could not write network file",e);
            }

        }
        log.info("Done processing windows");
        return results;
    }

    public List<Network> analyzeToMemory() {
        List<Network> results = new ArrayList<Network>();
        log.info("Will process " + bmodel.getNumWindows() + " windows");
        for (int win = 0; win < bmodel.getNumWindows(); win++) {
            log.debug("Process window " + win);

            results.add(analyzeWindow(win));
        }
        log.info("Done processing windows");
        return results;
    }

    public Network analyzeWindow(int win) {
        UndirectedJungNetwork graph = new UndirectedJungNetwork();
        List<Bin<T>> bins = bmodel.getDataAtWindow(win);

        for (Bin<T> bin : bins) {
            if (!bin.isEmpty()) {
                JungUtils.merge(graph, (Graph<Node, Edge>) calculator.calculateWeights(getTokens(0, bin.getFirstItemIndex(), bin), getTokens(bin.getFirstItemIndex(), bin.size(), bin)), merger);
            }

        }
        if (merger == JungUtils.MergePolicy.ADD) {
            JungUtils.normalizeEdgeWeights(graph);
        }
        return graph;
    }


    public List<String> getTokens(int start, int end, Bin<T> bin) {
        List<String> result = new ArrayList<String>();
        for (int i = start; i < end; i++) {
            result.addAll(bin.get(i).getTokens());
        }
        return result;


    }
}
