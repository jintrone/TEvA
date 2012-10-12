package edu.mit.cci.teva.serialization;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.teva.engine.CommunityFrame;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Collection;
import java.util.HashSet;

/**
* User: jintrone
* Date: 10/8/12
* Time: 4:40 PM
*/
@XmlRootElement(name = "Frame")
public class CommunityFrameJaxbAdapter extends XmlAdapter<CommunityFrameJaxbAdapter,CommunityFrame> {

    Collection<Edge> edges;
    Collection<Clique> cliques;
    int window;

    @Override
    public CommunityFrame unmarshal(CommunityFrameJaxbAdapter jaxbAdapter) throws Exception {

        CommunityFrame result = new CommunityFrame(jaxbAdapter.getWindow());
       for (Edge e:jaxbAdapter.getEdges()) {
           result.add(e);
       }
        for (Clique c:jaxbAdapter.getCliques()) {
            result.addClique(c);
        }
        return result;
    }

    @Override
    public CommunityFrameJaxbAdapter marshal(CommunityFrame communityFrame) throws Exception {
        return new CommunityFrameJaxbAdapter(communityFrame);
    }

    public CommunityFrameJaxbAdapter() {}

    public CommunityFrameJaxbAdapter(CommunityFrame frame) {
        this.edges = new HashSet<Edge>(frame.getEdges());
        this.cliques = new HashSet<Clique>(frame.getCliques());
        this.window = frame.getWindow();
    }

    public void setEdges(Collection<Edge> edges) {
        this.edges = edges;
    }
    @XmlElementWrapper(name = "edges")
    @XmlElement(name = "edge")
    public Collection<Edge> getEdges() {
        return edges;
    }

    public Collection<Clique> getCliques() {
        return cliques;
    }

    public void setCliques(Collection<Clique> cliques) {
        this.cliques = cliques;
    }

    @XmlAttribute
    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }
}
