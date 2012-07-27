package edu.mit.cci.teva.util;

import edu.mit.cci.teva.adapters.CFinderAdapter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * User: jintrone
 * Date: 7/26/12
 * Time: 1:10 PM
 */
public class CFinderBatchRunner {

    public static String DEFAULT_PROPS = "cfinderbatch.properties";

    public static String INPUTDIR_PROP = "inputdir";
    public static String EXTENSION_PROP = "extension";
    public static String IGNORE_EXISTING_PROP = "ignoreexisting";

    public static Logger log = Logger.getLogger(CFinderBatchRunner.class);

    Properties props;
    File inputdir;

    public CFinderBatchRunner() throws IOException {
      this(DEFAULT_PROPS);
    }

    public CFinderBatchRunner(Properties p) {
        this.props = p;
        process();
    }

    public CFinderBatchRunner(String propsFileName) throws IOException {
        props = new Properties();
        props.load(new FileReader(propsFileName));
        process();
    }

    public void process() {
       inputdir = new File(props.getProperty(INPUTDIR_PROP,"."));
       final String extension = props.getProperty(EXTENSION_PROP,null);
       if (!inputdir.exists() || !inputdir.isDirectory()) {
           throw new RuntimeException("Could not locate input directory: "+inputdir.getAbsolutePath());
       }

       File[] files = inputdir.listFiles(new FilenameFilter() {
           public boolean accept(File file, String s) {
               return extension == null || s.endsWith(extension);
           }
       });
      // MIN_WEIGHT("w"), MAX_WEIGHT("W"), CLIQUE_SIZE("k"), INTENSITY("I"), MAX_TIME("t"), DIRECTED("D");

        List<String> params = new ArrayList<String>();
        for (Enumeration e =props.propertyNames();e.hasMoreElements();) {
            String prop = (String)e.nextElement();

            if (CFinderAdapter.CommandLineParams.valueOf(prop)!=null) {
                params.add(prop);
                params.add(props.getProperty(prop));
            }
        }

        CFinderAdapter cfinder = new CFinderAdapter(params.toArray(new String[params.size()]));

        for (File f:files) {
            try {
                cfinder.process(f,Boolean.parseBoolean(props.getProperty(IGNORE_EXISTING_PROP,"false")));
            } catch (IOException e) {
                log.error(e);
            } catch (InterruptedException e) {
                log.error(e);
            }
        }



    }




}
