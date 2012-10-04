package edu.mit.cci.teva.engine;

import edu.mit.cci.teva.util.ScoringMethod;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 11:57 AM
 */
public class TevaParameters {


    private String CFinderLicensePath;

    /** Minimum link weight to be considered when extracting communities
     *
     * @return
     */
    public float getMinimumLinkWeight(float f) {
        return 0f;
    }

    /**
     * Clique size for community extraction
     * @return
     */
    public int getFixedCliqueSize(int def) {
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }


    /**
     * Whether or not communities that are "consumed" should continue to be considered progenitors for subsequent
     * communities
     * @return
     */
    public boolean expireConsumedCommunities() {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }


    /**
     * Scoring method to be used when extablishing the mapping between communities in subsequent time steps
     * @return
     */
    public ScoringMethod getScoringMethod() {
        return null;
    }

    public String getCFinderExecutable() {
        return "";
    }

    public String getCFinderLicensePath() {
        return "";
    }

    public String[] getCFinderCommandLine() {
        return new String[0];
    }
}
