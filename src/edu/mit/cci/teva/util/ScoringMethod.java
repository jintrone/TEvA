package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Network;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 10:27 PM
 */
public interface ScoringMethod {

    public float score(Network one, Network two);
}
