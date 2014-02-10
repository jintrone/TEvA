package edu.mit.cci.text.preprocessing;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/21/12
 * Time: 5:02 PM
 */
public class AlphaNumericTokenizer implements Tokenizer<String> {

    private static Logger log = Logger.getLogger(AlphaNumericTokenizer.class);
    Munger munger;


    public AlphaNumericTokenizer(Munger... mungers) {

        this.munger = new CompositeMunger(mungers);
    }

    public AlphaNumericTokenizer() {
        this.munger = null;
    }

    /**
     * Default implementation remove quote characters, replaces all other punctuation with spaces, and
     * compresses multiple spaces to single spaces
     * @param input
     * @return
     */
    protected String replace(String input) {
        String data = input.replaceAll("'", "");
        data = data.replaceAll("[^\\p{Alnum}]+", " ");
        data = data.replaceAll("\\s+", " ");
        data = data.replaceAll("\\s\\p{Alnum}\\s"," ");
        return data;
    }

    public List<String> tokenize(String input) {
        String data = replace(input);

        String[] words = data.split("\\s+");
        List<String> tokens = new ArrayList<String>();

        if (munger == null) {
            tokens.addAll(Arrays.asList(words));
        } else {
            log.debug("Input: "+Arrays.asList(words));
            for (String w : words) {

                w = w.trim();
                if (w.isEmpty()) continue;
                log.debug("Chew: " + w.toLowerCase());
                if (munger.read(w.toLowerCase())) {
                    List<String> spit = munger.flush();
                    log.debug("PTtooey! " + spit);
                    tokens.addAll(spit);
                }
            }
            List<String> l = munger.finish();
            log.debug("Finishing: " + l);
            tokens.addAll(l);
        }
        log.debug("Output: "+tokens);
        return tokens;
    }


}
