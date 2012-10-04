package edu.mit.cci.teva.engine;

/**
* User: jintrone
* Date: 10/2/12
* Time: 10:29 PM
*/
public class ScoredCommunityFramePair {

    CommunityFrame[] pair;
    float score;

    public ScoredCommunityFramePair(CommunityFrame one, CommunityFrame two, float score) {
      pair = new CommunityFrame[] {one,two};
        this.score = score;
    }

    public CommunityFrame[] getPair() {
        return pair;
    }
    public float getScore() {
        return score;
    }
}
