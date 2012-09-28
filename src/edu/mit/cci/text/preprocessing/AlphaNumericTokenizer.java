package edu.mit.cci.text.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/21/12
 * Time: 5:02 PM
 */
public class AlphaNumericTokenizer<T extends TextTransformable> implements Tokenizer<TextTransformable> {

    Munger munger;
    List<String> tokens;

    public AlphaNumericTokenizer(Munger... mungers) {

        this.munger = new CompositeMunger(mungers);
    }

    public AlphaNumericTokenizer() {
        this.munger = null;
    }

    public List<String> tokenize(TextTransformable input) {
        String data = input.transform();
        data = data.replaceAll("'", "");
        data = data.replaceAll("[^\\p{Alnum}]+", " ");
        data = data.replaceAll("\\s+", " ");
        String[] words = data.split("\\s+");

        if (munger == null) {
            tokens = Arrays.asList(words);
        }
        for (String w : words) {
            w = w.trim();
            if (w.isEmpty()) continue;
            if (munger.read(w.toLowerCase())) {
                tokens.addAll(munger.flush());
            }
        }
        tokens.addAll(munger.finish());
        return tokens;
    }



}
