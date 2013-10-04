package edu.mit.cci.teva.example;

import edu.mit.cci.adapters.csv.CsvBasedConversation;
import edu.mit.cci.teva.DefaultTevaFactory;
import edu.mit.cci.teva.MemoryBasedRunner;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.engine.TevaParameters;
import edu.mit.cci.teva.model.Conversation;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 12/12/12
 * Time: 11:17 PM
 */
public class RunSampleCsv {

    public static void main(String args[]) throws IOException, ParseException, JAXBException, CommunityFinderException {

        System.err.println(new File(".").getAbsoluteFile());
        Map<String,String> params = getParams(args);
        InputStream props = null;
        InputStream datafile = null;
        if (params.containsKey("p")) {
            props = new FileInputStream(params.get("p"));
        } else {

        }
        String corpus = "Demo";
        if (params.containsKey("f")) {
            File f = new File(params.get("f"));
            datafile = new FileInputStream(f);
            corpus = params.get(f.getName());
        } else {
            datafile = ClassLoader.getSystemClassLoader().getResourceAsStream("sampledata/MM15.csv");
        }
        Conversation c = new CsvBasedConversation(corpus,datafile);


        TevaParameters tevaParams = new TevaParameters(props);
        new MemoryBasedRunner(c,tevaParams,new DefaultTevaFactory(tevaParams,c));
    }

    public static void usage() {
        String usage = "USAGE:  RunSampleCsv [-p<properties_file>] [-f<input_csv>]";
        System.err.println(usage);
    }

    public static Map<String,String> getParams(String[] args) {
        Map<String,String> result = new HashMap<String,String>();


        for (String s:args) {

            if (s.startsWith("-p")) {
                result.put("p",s.substring(2));
            } else if (s.startsWith("-f")) {
                result.put("f",s.substring(2));

            } else if (s.startsWith("-help") || "-?".equals(s)) {
                usage();
                System.exit(1);
            } else if (s.startsWith("-")) {
                System.out.println("Unknown option: "+s);
            }

        }
        return result;
    }

}
