package edu.mit.cci.teva.engine;

/**
* User: jintrone
* Date: 10/14/12
* Time: 11:38 PM
*/
public class CommunityScore {
    float coverage = 0.0f;
    float similarity = 0.0f;
    int win = -1;
    Community community = null;

    public CommunityScore(int win, Community community, float coverage, float similarity) {
        this.coverage = coverage;
        this.similarity = similarity;
        this.win = win;
        this.community = community;
    }

}
