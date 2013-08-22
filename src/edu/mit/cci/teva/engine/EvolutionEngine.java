package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: jintrone
 * Date: 4/19/12
 * Time: 7:36 PM
 */
public class EvolutionEngine {


    private static Logger log = Logger.getLogger(EvolutionEngine.class);
    private static Logger logProcess = log;


    EvolutionStepStrategy stepStrategy;
    MergeStrategy mergeStrategy;

    private NetworkProvider provider;
    private CommunityFinder finder;


    private CommunityModel model;
    private TevaParameters params;


    public EvolutionEngine(CommunityModel model, TevaParameters params, NetworkProvider provider, CommunityFinder finder, EvolutionStepStrategy stepper, MergeStrategy merger) {
        this.model = model;
        this.provider = provider;
        this.stepStrategy = stepper;
        this.mergeStrategy = merger;
        this.finder = finder;
        this.params = params;
    }


    public CommunityModel getCommunityModel() {
        return model;
    }


    /**
     * At each step, uses the "maximal communities" (leading edge) of the current topic model
     * and tries to develop a mapping to the next slice of conversation, using Palla's approach
     * of merging slices and mapping through cliques in the merged layer
     *
     * @throws CommunityFinderException
     * @throws java.io.IOException
     */
    public void process() throws CommunityFinderException, IOException {


        Network lastGraph = null;
        for (int i = 0; i < provider.getNumberWindows(); i++) {

            Network currentGraph = provider.getNetworkAt(i);
            log.info("Removing edges weight < " + params.getMinimumLinkWeight() + " from " + currentGraph.getEdges().size());
            NetworkUtils.filterEdges(currentGraph, params.getMinimumLinkWeight());
            log.info(currentGraph.getEdges().size() + " edges remain");
            if (currentGraph.getEdges().size() == 0) {
                log.info("Skipping network at step " + i);
                continue;
            }
            if (!step(i, lastGraph, currentGraph)) {
                break;
            }
            lastGraph = currentGraph;

        }
    }

    protected int getCliqueSizeAtWindow(int i) {
        return params.getFixedCliqueSize();
    }

    protected List<CommunityFrame> getInputFrames() {
        List<CommunityFrame> result = new ArrayList<CommunityFrame>();
        Set<String> communities = new HashSet<String>();
        for (Community c : model.getCommunities()) {

            if (c.isExpired()) continue;
            if (communities.contains(c.getId())) {
                log.warn("Adding frame that already points to community: " + c.getId());
            }
            communities.add(c.getId());
            CommunityFrame data = c.getCommunityAtBin(c.getMaxBin());
            result.add(data);

        }
        return result;
    }

    private boolean step(int i, Network lastGraph, Network currentGraph) throws CommunityFinderException {

        List<CommunityFrame> to = finder.findCommunities(currentGraph, getCliqueSizeAtWindow(i), i, params.getFilenameIdentifier());
        if (to == null) return false;

        if (lastGraph == null) {


            for (CommunityFrame a : to) {
                Community c = Community.create();
                c.addFrame(a);
                model.addCommunity(c);
                log.info("CREATE INIT COMMUNITY " + c + " -> " + a.getNodes());
            }

        } else {


            List<CommunityFrame> from = getInputFrames();
            List<Network> merged = mergeStrategy.process(lastGraph, from, currentGraph, to, i);
            stepStrategy.processStep(i, from, merged, to);

        }
        return true;
    }


}
