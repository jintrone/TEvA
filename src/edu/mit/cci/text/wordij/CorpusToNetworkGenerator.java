package edu.mit.cci.text.wordij;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.text.preprocessing.TextTransformable;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.uci.ics.jung.graph.Graph;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 5/11/11
 * Time: 5:07 PM
 */

/**
 * Generates a list fo graphs
 *
 * @param <T>
 */
public class CorpusToNetworkGenerator<T extends TextTransformable> {

    BinningStrategy<T> bmodel;
    WindowStrategy<T> strategy;
    Tokenizer<T> tokenizer;
    TextToNetworkGenerator calculator;

    Logger log = Logger.getLogger(CorpusToNetworkGenerator.class);


    public CorpusToNetworkGenerator(BinningStrategy<T> bmodel,
                                    WindowStrategy<T> strategy,
                                    Tokenizer<T> tokenizer,
                                    TextToNetworkGenerator calculator) {
        this.bmodel = bmodel;
        this.strategy = strategy;
        this.calculator = calculator;
        this.tokenizer = tokenizer;
    }

    public Map<Integer, Network> analyze() {
        Map<Integer, Network> results = new LinkedHashMap<Integer, Network>();

        for (int win = 0; win < strategy.getNumberWindows(); win++) {
            UndirectedJungNetwork graph = new UndirectedJungNetwork();

            for (int bin = 0; bin < bmodel.getNumBins(win); bin++) {

                List<String> tokens = getTokens(bin,win);

                JungUtils.merge(graph, (Graph<Node, Edge>) calculator.calculateWeights(tokens), JungUtils.MergePolicy.ADD);
            }
            results.put(win, graph);


        }
        log.debug("Done processing windows");
        return results;
    }



    public List<String> getTokens(int bin, int window) {
        List<T> contents = bmodel.getBinContents(bin);
        strategy.setData(contents);
        List<T> windata = strategy.getWindow(window);
        List<String> result = new ArrayList<String>();
        for (T passage:windata) {
            result.addAll(tokenizer.tokenize(passage));
        }
        return result;


    }
}
