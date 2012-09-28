package edu.mit.cci.text.preprocessing;

/**
 * User: jintrone
 * Date: 10/20/11
 * Time: 12:46 PM
 */
public interface StringPreprocessor {

    public String process(String s);


    public static class Composite implements StringPreprocessor {

        StringPreprocessor[] preprocessors;


        public Composite(StringPreprocessor... p) {
           preprocessors = p;
        }

        public String process(String s) {
            String result = s;
            for (StringPreprocessor p:preprocessors) {
                result = p.process(result);
            }
            return result;
        }
    }

}
