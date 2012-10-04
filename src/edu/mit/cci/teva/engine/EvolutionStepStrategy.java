package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;

import java.util.List;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 9:33 PM
 */
public interface EvolutionStepStrategy {
    void processStep(int i, List<CommunityFrame> fromcliques, List<Network> mergedcliques, List<CommunityFrame> tocliques);

}
