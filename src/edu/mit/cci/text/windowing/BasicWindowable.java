package edu.mit.cci.text.windowing;

import edu.mit.cci.text.preprocessing.Tokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/14/12
 * Time: 11:07 PM
 */

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.NONE)
public class BasicWindowable implements Windowable {

    @XmlAttribute
    @XmlID
    public String id;

    @XmlElement
    public String content;

    @XmlAttribute
    private Date start;


    private Tokenizer<String> tokenizer;


    private BasicWindowable() {

    }

    public BasicWindowable(String id, String rawData, Date start) {
        this.start = start;
        this.id = id;
        this.content = rawData;
    }


    public Date getStart() {
        return start;
    }

    public void setTokenizer(Tokenizer<String> t) {
        this.tokenizer = t;
    }

    public List<String> getTokens() {
        return tokenizer.tokenize(content);
    }

    public String getRawData() {
        return content;
    }

    public String getId() {
        return id;
    }



    public static class JaxbAdapter extends XmlAdapter<BasicWindowable, Windowable> {
        @Override
        public Windowable unmarshal(BasicWindowable w) throws Exception {
            return w;
        }

        @Override
        public BasicWindowable marshal(Windowable windowable) throws Exception {
            return new BasicWindowable(windowable.getId(), windowable.getRawData(), windowable.getStart());
        }
    }
}
