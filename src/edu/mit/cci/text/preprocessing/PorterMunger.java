package edu.mit.cci.text.preprocessing;

import edu.mit.cci.teva.util.PorterStemmer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by josh on 9/16/14.
 */
public class PorterMunger implements Munger {

    PorterStemmer stemmer = new PorterStemmer();
    List<String> buffer = new ArrayList<String>();
    private static Logger logger = Logger.getLogger(PorterMunger.class);

    @Override
    public boolean read(String from) {
        if (from==null || "".equals(from.trim())) {
            return false;
        }
        String result = stemmer.stem(from);

        if ("".equals(result)) {
            return false;
        }
        buffer.add(result);
        return true;



    }

    @Override
    public List<String> flush() {
        List<String> result =  new ArrayList<String>(buffer);
        buffer.clear();
        return result;
    }

    @Override
    public List<String> finish() {
        return flush();
    }
}
