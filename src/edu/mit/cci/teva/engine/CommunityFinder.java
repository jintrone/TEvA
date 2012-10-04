package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 11:41 AM
 */
public interface CommunityFinder {

    List<CommunityFrame> findCommunities(File currentGraph, int cliqueSizeAtWindow, int window) throws CommunityFinderException;
    List<CommunityFrame> findCommunities(Network currentGraph, int cliqueSizeAtWindow, int window, String id) throws CommunityFinderException;
}
