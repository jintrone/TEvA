package edu.mit.cci.teva.cpm.cos;


import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.util.StreamGobbler;
import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: jintrone
 * Date: 11/13/12
 * Time: 4:17 PM
 */
public class CosRunner {

    public static final String DEFAULT_MAX_CLIQUES_EXECUTABLE = "/usr/local/bin/maximal_cliques";
    public static final String DEFAULT_COS_EXECUTABLE = "/usr/local/bin/cos";

    private static Logger log = Logger.getLogger(CosRunner.class);

    private String mCliqueExec;
    private String cosExec;
    private String outputBaseDir;
    private String cosParams = "";

    public CosRunner(String pathToMaxCliquesExecutable, String pathToCosExecutable, String outputBaseDir) {
        this.mCliqueExec = pathToMaxCliquesExecutable;
        this.cosExec = pathToCosExecutable;
        this.outputBaseDir = outputBaseDir;
    }

    public CosRunner(String pathToMaxCliquesExecutable, String pathToCosExecutable, String cosParams, String outputBaseDir) {
        this.mCliqueExec = pathToMaxCliquesExecutable;
        this.cosExec = pathToCosExecutable;
        this.outputBaseDir = outputBaseDir;
        this.cosParams = cosParams;
    }

    public String buildMCliqueCommandLine(File inputFile) {
        return String.format("%s %s", mCliqueExec, inputFile.getAbsolutePath());

    }

    public String buildCosCommandLine(File inputFile) {
        return String.format("%s %s %s", cosExec, cosParams, inputFile.getAbsolutePath());

    }


    public boolean process(File inputFile, boolean overwrite) throws IOException, InterruptedException, CommunityFinderException {
        File outputDir = inputFile.getParentFile();

        if (!new File(inputFile.getAbsolutePath() + ".mcliques").exists() || overwrite) {
            if (!generateMaxCliques(inputFile)) throw new CommunityFinderException("Problem generating max cliques");

            // move files

            boolean b = U.move(new File(inputFile.getName() + ".map"), outputDir);
            b &= U.move(new File(inputFile.getName() + ".mcliques"), outputDir);
            if (!b) {
                log.error("Error moving files");
                throw new CommunityFinderException("Could not move files to output dir: " + outputDir.getAbsolutePath());
            }
        }
        if (!new File(outputDir, "k_num_communities.txt").exists() || overwrite) {
            if (!generateCommunityAnalysis(inputFile, outputDir))
                throw new CommunityFinderException("Problem extracting communities");


            File[] files = new File(".").listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    return s.endsWith("_communities.txt");
                }
            });
            boolean b = true;
            for (File f : files) {
                b &= U.move(f, outputDir);
            }
            if (!b) {
                throw new CommunityFinderException("Error moving community files");
            }
        }
        return true;


    }

    private boolean generateMaxCliques(File inputFile) throws IOException, InterruptedException {
        log.info("Generating maximal cliques for " + inputFile.getAbsolutePath());
        long current = System.currentTimeMillis();
        String cmd = buildMCliqueCommandLine(inputFile);
        log.info("Run " + cmd);
        Process p = Runtime.getRuntime().exec(cmd);

        StreamGobbler errorGobbler = new
                StreamGobbler(p.getErrorStream(), "ERROR");

        // any output?
        StreamGobbler outputGobbler = new
                StreamGobbler(p.getInputStream(), "OUTPUT");

        // kick them off
        errorGobbler.start();
        outputGobbler.start();

        // any error???
        int exitVal = p.waitFor();
        log.debug("ExitValue: " + exitVal);

        if (log.isDebugEnabled()) {
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = null;
            while ((s = r.readLine()) != null) {
                System.err.println(s);
            }
        }

        log.info("Finished in " + (System.currentTimeMillis() - current) / 1000l + " seconds.");


        return true;
    }

    private boolean generateCommunityAnalysis(File inputFile, File outputdir) throws IOException, InterruptedException {
        log.info("Generating communities for " + inputFile.getName());
        long current = System.currentTimeMillis();

        File mcliques = new File(outputdir, inputFile.getName() + ".mcliques");
        String cmd = buildCosCommandLine(mcliques);
        log.info("Execute: " + cmd);
        Process p = Runtime.getRuntime().exec(cmd);
        StreamGobbler errorGobbler = new
                StreamGobbler(p.getErrorStream(), "ERROR");

        // any output?
        StreamGobbler outputGobbler = new
                StreamGobbler(p.getInputStream(), "OUTPUT");

        // kick them off
        errorGobbler.start();
        outputGobbler.start();

        // any error???
        int exitVal = p.waitFor();
        log.debug("ExitValue: " + exitVal);

        if (log.isDebugEnabled()) {
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = null;
            while ((s = r.readLine()) != null) {
                System.err.println(s);
            }
        }

        log.info("Finished in " + (System.currentTimeMillis() - current) / 1000l + " seconds.");


        return true;
    }

    private File checkExists(File inputFile, String s) {
        File f = new File(inputFile.getParentFile(), inputFile.getName() + s);
        if (f.exists()) return f;
        else return null;
    }


}
