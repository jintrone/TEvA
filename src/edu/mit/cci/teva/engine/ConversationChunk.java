package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.text.windowing.Windowable;

import javax.xml.bind.annotation.*;
import java.util.Collection;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/14/12
 * Time: 11:02 PM
 */

@XmlRootElement(name = "chunk")
@XmlAccessorType(XmlAccessType.NONE)
public class ConversationChunk {

    @XmlElement(name = "post")
    public List<Windowable> messages;

    @XmlAttribute
    public float coverage;
    @XmlAttribute
    public float similarity;

    public Collection<Edge> edges;


    @XmlElementWrapper(name = "edges")
    @XmlElement(name = "edge")
    public Collection<Edge> getEdges() {
        return edges;
    }

    @XmlAttribute
    public int window;

    public ConversationChunk() {}

    public ConversationChunk(List<Windowable> messages, int window, float coverage, float similarity) {
        this.coverage = coverage;
        this.window = window;
        this.similarity = similarity;
        this.messages = messages;
    }

    public ConversationChunk(List<Windowable> messages, int window, float coverage, float similarity,Collection<Edge> edges) {
        this(messages,window,coverage,similarity);
        this.edges = edges;
    }

    public void setEdges(Collection<Edge> edges) {
        this.edges = edges;
    }





}
