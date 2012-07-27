package edu.mit.cci.teva.adapters;

import edu.mit.cci.util.StreamGobbler;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 6/15/11
 * Time: 11:11 AM
 */
public class CFinderAdapter {


    private static Logger log = Logger.getLogger(CFinderAdapter.class);
    public static String DEFAULT_CFINDER_EXECUTABLE = "/usr/local/bin/cfinder";
    public static String DEFAULT_CFINDER_LICENSE = "/Applications/CFinder-v2.0.5/licence.txt";

    public String getCfinderExecutablePath() {
        return cfinderExecutablePath;
    }

    public void setCfinderExecutablePath(String cfinderExecutablePath) {
        this.cfinderExecutablePath = cfinderExecutablePath;
    }

    public String getCfinderLicensePath() {
        return cfinderLicensePath;
    }

    public void setCfinderLicensePath(String cfinderLicensePath) {
        this.cfinderLicensePath = cfinderLicensePath;
    }

    public String cfinderExecutablePath = DEFAULT_CFINDER_EXECUTABLE;
    public String cfinderLicensePath = DEFAULT_CFINDER_LICENSE;

    public Map<CommandLineParams, String> params = new HashMap<CommandLineParams, String>();

    private String directory_suffix;
    private String info_prefix;

    public enum CommandLineParams {
        MIN_WEIGHT("w"), MAX_WEIGHT("W"), CLIQUE_SIZE("k"), INTENSITY("I"), MAX_TIME("t"), DIRECTED("D");


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
            return  result;
        }


    }

    public CFinderAdapter() {
        this("MIN_WEIGHT", ".5");
    }

    public CFinderAdapter(String... pmap) {
        setParams(pmap);
    }

    public void clearParam(CommandLineParams p) {
        params.remove(p);
    }



    public void setParams(String[] pmap) {
        params.clear();
        for (int i = 0;i<pmap.length;i+=2) {

            CommandLineParams p = CommandLineParams.resolve(pmap[i]);
            if (p == null) {
                log.warn("Do not recognize " + pmap[i] + "; ignoring");
            } else {
                setParam(p, pmap[i + 1]);
            }
        }
    }

    public void setParam(CommandLineParams p, String value) {
        params.put(p,value);
    }

    public String buildCommandLine(File f) {
        String cline = cfinderExecutablePath;
        for (Map.Entry<CommandLineParams, String> ent : params.entrySet()) {
            cline = ent.getKey().format(cline, ent.getValue());
        }
        cline += " -l " + cfinderLicensePath;
        return cline + " -i " + f.getAbsolutePath();

    }

    public boolean process(File f, boolean force) throws IOException, InterruptedException {
        if (checkCliqueDir(f, force)) {
            log.info("Generating communities for " + f.getName());
            long current = System.currentTimeMillis();
            Process p = Runtime.getRuntime().exec(buildCommandLine(f));
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
        return false;
    }

    /**
     * returns true if cliquedir is avaiable, optionally deleting if it existis
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
                    delete(td);
                }
            } else {
                return false;
            }

        }


        return true;
    }

    public void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }


}
