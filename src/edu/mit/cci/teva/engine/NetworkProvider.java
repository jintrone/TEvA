package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 11:19 AM
 */
public interface NetworkProvider {

    public int getNumberWindows();
    public Network getNetworkAt(int i);
}
