package edu.mit.cci.teva.engine;


import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.CliqueDecoratedNetwork;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.serialization.CommunityFrameJaxbAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: jintrone
 * Date: 6/15/11
 * Time: 1:27 PM
 */

@XmlJavaTypeAdapter(CommunityFrameJaxbAdapter.class)
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

        this.window = window;
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

        return "CF:"+ ((community==null?"(no community)":"("+community.getId()+")")+getWindow());
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



    public int hashCode() {
        return (this.getEdges().hashCode() + window)*13 + 7;
    }

    public boolean equals(CommunityFrame cf) {
        return cf.window == this.window &&
                this.getEdges().equals(cf.getEdges());
    }


}
