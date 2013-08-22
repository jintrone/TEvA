package edu.mit.cci.teva.cpm.cos;

import edu.mit.cci.sna.CliqueDecoratedNetwork;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.engine.CommunityFinder;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.engine.CommunityFrame;
import edu.mit.cci.teva.engine.TevaParameters;
import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jintrone
 * Date: 11/13/12
 * Time: 4:01 PM
 */
public class CosCommunityFinder implements CommunityFinder {

    private static Logger log = Logger.getLogger(CosCommunityFinder.class);

    private static final String COS_NET_NAME = "CosNetwork";
    private boolean overwriteNetworks = false;
    private boolean overwriteAnalysis = false;
    private File outputdir;
    private CosRunner cosRunner;

    public CosCommunityFinder(TevaParameters params) {
        this.overwriteNetworks = params.getOverwriteNetworks();
        this.overwriteAnalysis = params.getOverwriteAnalyses();
        this.outputdir = new File(params.getWorkingDirectory());
        this.cosRunner = new CosRunner(params.getCosMaxCliquesExecutable(), params.getCosExecutable(), params.getCpmParameters(), params.getWorkingDirectory());

    }


    public List<CommunityFrame> findCommunities(File networkFile, int cliqueSizeAtWindow, int window) throws CommunityFinderException {
        try {
             cosRunner.process(networkFile, overwriteAnalysis);

        } catch (IOException e) {
            throw new CommunityFinderException("Error processing network", e);
        } catch (InterruptedException e) {
            throw new CommunityFinderException("Error running cos", e);
        }

        List<CliqueDecoratedNetwork> networks = null;

        try {
            networks = CosFileReader.readCommunities(networkFile.getName(), cliqueSizeAtWindow, networkFile.getParentFile());
        } catch (CommunityFinderException ex) {
            log.error(ex);
            log.info("No communities identified; continuing");
            networks = Collections.emptyList();
        }

        List<CommunityFrame> result = new ArrayList<CommunityFrame>();
        for (CliqueDecoratedNetwork n : networks) {
            result.add(new CommunityFrame(window, n));
        }
        return result;

    }

    public List<CommunityFrame> findCommunities(Network currentGraph, int cliqueSizeAtWindow, int window, String id) throws CommunityFinderException {
        File networkFile = getInputNetworkFile(this.outputdir, window, id);
        File outputDir = getOutputDir(networkFile);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File outputFile = new File(getOutputDir(networkFile), networkFile.getName());
        if (!outputFile.exists() || overwriteNetworks) {
            try {
               // U.delete(networkFile);
                log.debug("Attempting to create file at: " + networkFile.getAbsolutePath());
                NetworkUtils.createNetworkFile(currentGraph, outputFile, false);
            } catch (IOException e) {
                throw new CommunityFinderException("Error creating network file", e);
            }

        }
        return findCommunities(outputFile, cliqueSizeAtWindow, window);
    }

    public static File getInputNetworkFile(File outputdir, int window, String id) {
        return new File(outputdir, COS_NET_NAME + "." + id + "." + window + ".net");
    }

    public static File getOutputDir(File inputfile) {
        return new File(inputfile.getAbsolutePath() + "_files");

    }
}
