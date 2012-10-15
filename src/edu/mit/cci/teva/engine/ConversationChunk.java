package edu.mit.cci.teva.engine;

import edu.mit.cci.text.windowing.Windowable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/14/12
 * Time: 11:02 PM
 */

@XmlRootElement(name = "chunk")
public class ConversationChunk {

    @XmlElement(name = "post")
    public List<Windowable> messages;

    @XmlAttribute
    public float coverage;
    @XmlAttribute
    public float similarity;

    @XmlAttribute
    public int window;

    private ConversationChunk() {}

    public ConversationChunk(List<Windowable> messages, int window, float coverage, float similarity) {
        this.coverage = coverage;
        this.window = window;
        this.similarity = similarity;
        this.messages = messages;
    }



}
