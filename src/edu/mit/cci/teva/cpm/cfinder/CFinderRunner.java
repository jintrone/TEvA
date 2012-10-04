package edu.mit.cci.teva.cpm.cfinder;

import edu.mit.cci.util.StreamGobbler;
import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 6/15/11
 * Time: 11:11 AM
 */
public class CFinderRunner {


    private static Logger log = Logger.getLogger(CFinderRunner.class);
    public static String DEFAULT_CFINDER_EXECUTABLE = "/usr/local/bin/cfinder";
    public static String DEFAULT_CFINDER_LICENSE = "/Applications/CFinder-v2.0.5/licence.txt";


    public Map<CommandLineParams, String> params = new HashMap<CommandLineParams, String>();

    private String directory_suffix;
    private String info_prefix;
    private File cfinderExecutablePath;
    private File cfinderLicensePath;


    public enum CommandLineParams {
        MIN_WEIGHT("w"), MAX_WEIGHT("W"), CLIQUE_SIZE("k"), INTENSITY("I"), MAX_TIME("t"), DIRECTED("D"), OUTPUT("o");


        String param;

        CommandLineParams(String param) {
            this.param = param;
        }

        public String format(String s, String val) {
            return s + " -" + param + " " + val;
        }

        public String dirSuffix(String val) {
            return val;
        }

        public String info_prefix() {
            return "";
        }

        public static CommandLineParams resolve(String s) {
            CommandLineParams result = null;
            try {
                result = CommandLineParams.valueOf(s);

            } catch (IllegalArgumentException e) {
                return null;
            }
            return result;
        }


    }

    public CFinderRunner(String pathToExectutable, String pathToLicense, String... params) {

        this.cfinderExecutablePath = new File(pathToExectutable);
        this.cfinderLicensePath = new File(pathToLicense);
        setParams(params);
    }


    public void setParams(String[] pmap) {
        params.clear();
        for (int i = 0; i < pmap.length; i += 2) {

            CommandLineParams p = CommandLineParams.resolve(pmap[i]);
            if (p == null) {
                log.warn("Do not recognize " + pmap[i] + "; ignoring");
            } else {
                setParam(p, pmap[i + 1]);
            }
        }
    }

    public void setParam(CommandLineParams p, String value) {
        params.put(p, value);
    }

    public String buildCommandLine(File inputFile) {
        String cline = cfinderExecutablePath.getAbsolutePath();
        for (Map.Entry<CommandLineParams, String> ent : params.entrySet()) {
            cline = ent.getKey().format(cline, ent.getValue());
        }
        cline += " -l " + cfinderLicensePath;
        return cline + " -i " + inputFile.getAbsolutePath();

    }

    public boolean process(File inputFile, boolean overwrite) throws IOException, InterruptedException {
        if (getOutputDir(inputFile).exists() && overwrite) {
            U.delete(getOutputDir(inputFile));
        } else {
            log.warn("Output directory exists, skipping community generation");
            return false;
        }

        log.info("Generating communities for " + inputFile.getName());
        long current = System.currentTimeMillis();
        Process p = Runtime.getRuntime().exec(buildCommandLine(inputFile));
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



    public File getOutputDir(File inputfile) {
        if (params.containsKey(CommandLineParams.OUTPUT)) {
            return new File(params.get(CommandLineParams.OUTPUT));
        } else return new File(inputfile.getAbsolutePath() + "_files");

    }


    /**
     * returns true if cliquedir is avaiable, optionally deleting if it existis
     *
     * @param f
     * @param delete
     * @return
     * @throws IOException
     */
    public boolean checkCliqueDir(final File f, boolean delete) throws IOException {
        File[] fs = f.getParentFile().listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.getName().startsWith(f.getName()) && !file.equals(f));
            }
        });

        if (fs.length > 0) {
            if (delete) {
                for (File td : fs) {
                    U.delete(td);
                }
            } else {
                return false;
            }

        }


        return true;
    }


}
