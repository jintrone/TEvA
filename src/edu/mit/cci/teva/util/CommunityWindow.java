package edu.mit.cci.teva.util;

import edu.mit.cci.teva.engine.Community;

/**
* User: jintrone
* Date: 5/14/13
* Time: 6:18 AM
*/
public class CommunityWindow {
    public int window = -1;
    public String community = null;

    public CommunityWindow(String community, int window) {
        this.community = community;
        this.window = window;
    }

    public String toString() {
        return "C("+community+")W("+window+")";
    }

    public boolean equals(Object o) {
        if (o instanceof CommunityWindow) {
            CommunityWindow other = ((CommunityWindow)o);
            return other.window == window && other.community.equals(community);
        }
        return false;
    }

    public int hashCode() {
        return CommunityWindow.class.hashCode()+window*community.hashCode()*13+7;
    }



}
