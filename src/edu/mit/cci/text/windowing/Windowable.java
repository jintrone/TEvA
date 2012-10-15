package edu.mit.cci.text.windowing;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

/**
* User: jintrone
* Date: 9/24/12
* Time: 11:42 PM
*/

@XmlJavaTypeAdapter(BasicWindowable.JaxbAdapter.class)
public interface Windowable {
    public Date getStart();
    public List<String> getTokens();
    public String getRawData();
    public String getId();
}
