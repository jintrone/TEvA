package edu.mit.cci.teva.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
* User: jintrone
* Date: 5/14/13
* Time: 6:19 AM
*/
public class CommunityWindows {
    public Set<Integer> windows = new HashSet<Integer>();
    public String community = null;

    public CommunityWindows(String community, int... window) {
        this.community = community;
        for (int w : window) {
            windows.add(w);
        }
    }

    public CommunityWindows(String community, Collection<Integer> w) {
        this.community = community;
        windows.addAll(windows);

    }

    public void add(int win) {
        windows.add(win);
    }

}
