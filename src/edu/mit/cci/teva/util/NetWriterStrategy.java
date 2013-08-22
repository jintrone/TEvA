package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Network;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 6/3/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NetWriterStrategy {

    public File writeNetwork(Network network, int window) throws IOException;
    public File getNetworkFilename(int window);
}
