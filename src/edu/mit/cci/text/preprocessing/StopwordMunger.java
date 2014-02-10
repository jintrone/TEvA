package edu.mit.cci.text.preprocessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: jintrone
 * Date: 5/23/11
 * Time: 12:12 PM
 */
public class StopwordMunger implements Munger {

    public static final String[] defaultdata = new String[]{"a", "i", "a's", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "ain't", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "aren't", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "c'mon", "cmon", "c's", "cs", "came", "can", "can't", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldn't", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didn't", "didnt", "different", "do", "does", "doesn't", "doesnt", "doing", "don't", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "fifth", "fir", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadn't", "hadnt", "happens", "hardly", "has", "hasn't", "hasnt", "have", "haven't", "havent", "having", "he", "he's", "hes", "hello", "help", "hence", "her", "here", "here's", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i'd", "id", "i'll", "ill", "i'm", "im", "i've", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isn't", "isnt", "it", "it'd", "itd", "it'll", "itll", "it's", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "let's", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "mr", "mrs", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "EncodingPrintWriter.out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldn't", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "t's", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "taht", "that", "that's", "thats", "thats", "the", "hte", "their", "theirs", "them", "themselves", "then", "thence", "there", "thereat", "there's", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "they'd", "theyd", "they'll", "theyll", "they're", "theyre", "they've", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "ot", "ti", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasn't", "wasnt", "way", "we", "we'd", "wed", "we'll", "well", "we're", "were", "we've", "weve", "welcome", "well", "went", "were", "weren't", "werent", "what", "what's", "whats", "whatever", "when", "whence", "whenever", "where", "where's", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "who's", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "w", "with", "within", "without", "won't", "wont", "wonder", "would", "would", "wouldn't", "wouldnt", "yes", "yet", "you", "you'd", "youd", "you'll", "youll", "you're", "youre", "you've", "youve", "your", "yours", "yourself", "yourselves", "zero"};
    private Set<String> stopwords = new HashSet<String>();

    protected List<String> buffer = new ArrayList<String>();


    public StopwordMunger(String[] words, boolean useDefault) {
        if (useDefault) {
            for (String s : defaultdata) {
                stopwords.add(s);
            }

        }
        if (words != null) {
            for (String s : words) {
                stopwords.add(s);
            }
        }
    }

    public static StopwordMunger readAsNew(InputStream is) throws IOException {
        List<String> result = new ArrayList<String>();
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while ((line = reader.readLine())!=null) {
           result.addAll(Arrays.asList(line.trim().split(" ")));
        }
        return new StopwordMunger(result.toArray(new String[result.size()]),false);
    }

    public static StopwordMunger readAndAdd(InputStream is) throws IOException {
        List<String> result = new ArrayList<String>();
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while ((line = reader.readLine())!=null) {
            result.addAll(Arrays.asList(line.trim().split(" ")));
        }
        return new StopwordMunger(result.toArray(new String[result.size()]),true);
    }


    public boolean read(String from) {
        String result = process(from);
        if (result != null) {
            buffer.add(result);
            return true;
        }
        else return false;


    }

    public List<String> flush() {
        List<String> result = new ArrayList<String>(buffer);
        buffer.clear();
        return result;
    }

    public List<String> finish() {
        return Collections.emptyList();
    }

    protected String process(String from) {
        return (stopwords.contains(from.toLowerCase())) ? null : from;
    }
}
