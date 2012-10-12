package edu.mit.cci.teva.engine;

import edu.mit.cci.teva.util.ScoringMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 11:57 AM
 */
public class TevaParameters extends Properties {


    public static String MINIMUM_LINK_WEIGHT = "minimum_link_weight";
    public static String FIXED_CLIQUE_SIZE = "fixed_clique_size";
    public static String EXPIRE_CONSUMED_COMMUNITIES = "expire_consumed_communities";
    public static String SCORING_METHOD = "scoring_method";
    public static String CFINDER_EXECUTABLE_PATH = "cfinder_executable_path";
    public static String CFINDER_LICENSE_PATH="cfinder_license_path";
    public static String FILENAME_IDENITIFIER = "filename_identifier";
    public static final String WORD_REPLACEMENT_DICTIONARY = "word_replacement_dictionary";
    public static final String STOPWORD_LIST = "stopword_list";
    public static final String SLIDING_WINDOW_DELTA = "sliding_window_delta";
    public static final String SLIDING_WINDOW_SIZE = "sliding_window_size";
    public static final String WORDIJ_INDIRECTION_SIZE = "wordij_indirection_size";
    public static final String WORDIJ_TUPLE_SIZE = "wordij_tuple_size";
    public static final String WORKING_DIRECTORY = "working_directory";
    private static final String OVERWRITE_NETWORKS = "overwrite_networks";
    private static final String OVERWRITE_ANALYSES  = "overwrite_analyses";
    private static final String SKIP_NETWORK_GENERATION = "skip_network_generation";


    public TevaParameters(InputStream input) throws IOException {
        super();
        this.load(input);
    }

    public TevaParameters() throws IOException {
        super();
        this.load(getClass().getResourceAsStream("/teva.properties"));

    }



    /** Minimum link weight to be considered when extracting communities
     *
     * @return
     */
    public float getMinimumLinkWeight() {
        return Float.parseFloat(getProperty(MINIMUM_LINK_WEIGHT, ".5"));
    }

    public void setMinimumLinkWeight(float f) {
        this.setProperty(MINIMUM_LINK_WEIGHT,f+"");
    }

    /**
     * Clique size for community extraction
     * @return
     */
    public int getFixedCliqueSize() {
        return Integer.parseInt(getProperty(FIXED_CLIQUE_SIZE,"4"));
    }

    public void setFixedCliqueSize(int i) {
        this.setProperty(FIXED_CLIQUE_SIZE,i+"");
    }


    /**
     * Whether or not communities that are "consumed" should continue to be considered progenitors for subsequent
     * communities
     * @return
     */
    public boolean expireConsumedCommunities() {
        return Boolean.parseBoolean(getProperty(EXPIRE_CONSUMED_COMMUNITIES,"false"));
    }

    public void setExpireConsumedCommunities(boolean b) {
        this.setProperty(EXPIRE_CONSUMED_COMMUNITIES,""+b);
    }


    /**
     * Scoring method to be used when establishing the mapping between communities in subsequent time steps
     * @return
     */
    public ScoringMethod getScoringMethod() {
        String method = getProperty(SCORING_METHOD,"SIMILARITY");
        return ScoringMethod.Method.valueOf(method);
    }

    public void setScoringMethod(String method) {
        setProperty(SCORING_METHOD,method);
    }

    public String getCFinderExecutable() {
        return getProperty(CFINDER_EXECUTABLE_PATH);
    }

    public void setCFinderExecutable(String s) {
        setProperty(CFINDER_EXECUTABLE_PATH,s);
    }

    public String getCFinderLicensePath() {
        return getProperty(CFINDER_LICENSE_PATH);
    }

    public void setCFinderLicensePath(String path) {
        setProperty(CFINDER_LICENSE_PATH,path);
    }

    public String getFilenameIdentifier() {
        return getProperty(FILENAME_IDENITIFIER,"TEvA");
    }

    public void setFilenameIdentifier(String id) {
        setProperty(FILENAME_IDENITIFIER,id);
    }

    public static void main(String[] args) throws IOException {
        TevaParameters params = new TevaParameters();
        params.setCFinderExecutable("/usr/local/bin/cfinder");
        params.setCFinderLicensePath("/Applications/CFinder-v2.0.5/licence.txt");
        params.setExpireConsumedCommunities(true);
        params.setFilenameIdentifier("TEvA");
        params.setMinimumLinkWeight(.5f);
        params.setFixedCliqueSize(4);
        params.setScoringMethod("SIMILARITY");

        params.store(new FileWriter("resources/teva.properties"), "resources/teva.properties");


    }

    public String getReplacementDictionary() {
        return getProperty(WORD_REPLACEMENT_DICTIONARY,null);
    }

    public String getStopwordList() {
        return getProperty(STOPWORD_LIST,null);
    }

    public long getWindowSize() {
        return Long.parseLong(getProperty(SLIDING_WINDOW_SIZE, "-1"));
    }

    public long getWindowDelta() {
        return Long.parseLong(getProperty(SLIDING_WINDOW_DELTA,"-1"));
    }

    public int getWordijIndirection() {
        return Integer.parseInt(getProperty(WORDIJ_INDIRECTION_SIZE,3+""));
    }

    public int getWordijTupleSize() {

        return Integer.parseInt(getProperty(WORDIJ_TUPLE_SIZE,2+""));
    }

    public String getWorkingDirectory() {
        return getProperty(WORKING_DIRECTORY,"/temp");
    }

    public boolean getOverwriteNetworks() {
        return Boolean.parseBoolean(getProperty(OVERWRITE_NETWORKS, false + ""));
    }

    public boolean getOverwriteAnalyses() {
        return Boolean.parseBoolean(getProperty(OVERWRITE_ANALYSES, true + ""));
    }

    public boolean getSkipNetworkGeneration() {
        return Boolean.parseBoolean(getProperty(SKIP_NETWORK_GENERATION,false+""));
    }
}