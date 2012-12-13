package edu.mit.cci.teva.example;

import edu.mit.cci.adapters.csv.CsvBasedConversation;
import edu.mit.cci.teva.MemoryBasedRunner;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.model.Conversation;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * User: jintrone
 * Date: 12/12/12
 * Time: 11:17 PM
 */
public class RunSampleCsv {

    public static void main(String args[]) throws IOException, ParseException, JAXBException, CommunityFinderException {
        Object o = new Object();
        Conversation c = new CsvBasedConversation("MM15",o.getClass().getResourceAsStream("/sampledata/MM15.csv"));
        new MemoryBasedRunner(c);
    }

}
