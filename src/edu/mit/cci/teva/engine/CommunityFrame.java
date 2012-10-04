package edu.mit.cci.teva.engine;


import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.CliqueDecoratedNetwork;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: jintrone
 * Date: 6/15/11
 * Time: 1:27 PM
 */
public class CommunityFrame extends UndirectedJungNetwork {

    public static int id = -1;

    public static CommunityFrame EMPTY = new CommunityFrame(-1) {
        public String toString() {
            return "CF: Empty Frame";
        }
    };

    public Set<Clique> cliques = new HashSet<Clique>();


    private Community community = null;
    private int window = -1;

    public CommunityFrame(int bin) {
        this.window = bin;
    }

    public CommunityFrame(int window, CliqueDecoratedNetwork network) {
        for (Edge e:network.getEdges()) {
            this.add(e);
        }
        for (Clique c:network.getCliques()) {
            this.addClique(c);
        }
    }


    public int getWindow() {
        return window;
    }

    public String toString() {

        return "CF:"+ (community==null?"(no community)":community.getId()+getWindow());
    }

    public Community getCommunity() {
        return community;
    }



    public void setCommunity(Community community) {
        this.community = community;
    }

    public void addClique(Clique c) {
        cliques.add(c);
    }

    public Set<Clique> getCliques() {
        return cliques;
    }
}
