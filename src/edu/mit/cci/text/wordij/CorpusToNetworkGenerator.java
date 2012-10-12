package edu.mit.cci.text.wordij;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.uci.ics.jung.graph.Graph;
import org.apache.log4j.Logger;

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

    BinningStrategy<T> bmodel;
    TextToNetworkGenerator calculator;

    Logger log = Logger.getLogger(CorpusToNetworkGenerator.class);


    public CorpusToNetworkGenerator(BinningStrategy<T> bmodel,
                                    TextToNetworkGenerator calculator) {
        this.bmodel = bmodel;
        this.calculator = calculator;

    }

    public List<Network> analyze() {
        List<Network> results = new ArrayList<Network>();
        log.info("Will process "+bmodel.getNumWindows()+" windows");
        for (int win = 0; win < bmodel.getNumWindows(); win++) {
            log.debug("Process window "+win);
            results.add(analyzeWindow(win));
        }
        log.info("Done processing windows");
        return results;
    }

    public Network analyzeWindow(int win) {
        UndirectedJungNetwork graph = new UndirectedJungNetwork();
        List<List<T>> bins = bmodel.getDataAtWindow(win);

        for (List<T> bin : bins) {
            if (!bin.isEmpty()) {
                JungUtils.merge(graph, (Graph<Node, Edge>) calculator.calculateWeights(getTokens(bin)), JungUtils.MergePolicy.ADD_MAX_1);
            }
        }
        return graph;
    }


    protected List<String> getTokens(List<T> windata) {
        List<String> result = new ArrayList<String>();
        for (T passage : windata) {
            result.addAll(passage.getTokens());
        }
        return result;


    }
}
