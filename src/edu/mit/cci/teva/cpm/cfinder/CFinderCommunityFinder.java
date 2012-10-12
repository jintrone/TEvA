package edu.mit.cci.teva.cpm.cfinder;

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
import java.util.List;

/**
 * User: jintrone
 * Date: 10/3/12
 * Time: 10:08 PM
 */
public class CFinderCommunityFinder implements CommunityFinder {


    private static Logger log = Logger.getLogger(CFinderCommunityFinder.class);
    private static String CFINDER_NET_NAME = "CFinderNetwork";

    private File outputdir;


    private CFinderRunner cFinderRunner;
    private boolean overwriteAnalysis = false;
    private boolean overwriteNetworks = false;



    public CFinderCommunityFinder(boolean overwriteNetworks, boolean overwriteAnalysis, TevaParameters params) {
        this.overwriteNetworks = overwriteNetworks;
        this.overwriteAnalysis = overwriteAnalysis;
        this.outputdir = new File(params.getWorkingDirectory());
        cFinderRunner = new CFinderRunner(params.getCFinderExecutable(), params.getCFinderLicensePath());
        cFinderRunner.setParam(CFinderRunner.CommandLineParams.CLIQUE_SIZE, "" + params.getFixedCliqueSize());
        cFinderRunner.setParam(CFinderRunner.CommandLineParams.MIN_WEIGHT, "" + params.getMinimumLinkWeight());
    }


    public static File getNetworkFile(File outputdir, int window, String id) {
        return new File(outputdir, CFINDER_NET_NAME + "." + id + "." + window+".net");
    }

    public File getAnalysisOutputDirectory(File networkFile) {
        return new File(networkFile.getAbsolutePath() + "_files");
    }

    public List<CommunityFrame> findCommunities(File networkFile, int cliqueSizeAtWindow, int window) throws CommunityFinderException {
        File baseAnalysisDir = getAnalysisOutputDirectory(networkFile);
        File analysisDir = new File(baseAnalysisDir, "/k=" + cliqueSizeAtWindow);
        try {
            if (analysisDir.exists() && overwriteAnalysis) {
                U.delete(analysisDir);
            }
            if (!analysisDir.exists()) {
                cFinderRunner.setParam(CFinderRunner.CommandLineParams.OUTPUT, baseAnalysisDir.getAbsolutePath());
                cFinderRunner.process(networkFile, false);
            }
        } catch (IOException ex) {
            throw new CommunityFinderException("Error processing network files", ex);
        } catch (InterruptedException e) {
            throw new CommunityFinderException("Error running cfinder", e);
        }
        List<CliqueDecoratedNetwork> networks = null;
        try {
            networks = CFinderReader.readCommunities(cliqueSizeAtWindow, baseAnalysisDir);
        } catch (IOException ex) {
            throw new CommunityFinderException("Error reading analysis files");
        }

        List<CommunityFrame> result = new ArrayList<CommunityFrame>();
        for (CliqueDecoratedNetwork n : networks) {
            result.add(new CommunityFrame(window, n));
        }
        return result;

    }


    public List<CommunityFrame> findCommunities(Network currentGraph, int cliqueSizeAtWindow, int window, String id) throws CommunityFinderException {
        File networkFile = getNetworkFile(this.outputdir,window, id);
        if (!networkFile.exists() || overwriteNetworks) {
            try {
                U.delete(networkFile);
                log.debug("Attempting to create file at: "+networkFile.getAbsolutePath());
                NetworkUtils.createNetworkFile(currentGraph, networkFile);
            } catch (IOException e) {
                throw new CommunityFinderException("Error creating network file",e);
            }

        }
        return findCommunities(networkFile, cliqueSizeAtWindow, window);
    }



}
