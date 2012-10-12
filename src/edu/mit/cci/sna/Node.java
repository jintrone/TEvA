package edu.mit.cci.sna;

import edu.mit.cci.sna.impl.NodeImpl;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

/**
 * User: jintrone
 * Date: 9/21/12
 * Time: 9:06 PM
 */

@XmlJavaTypeAdapter(NodeImpl.JaxbAdapter.class)
public interface Node {

    public String getLabel();
    public void setLabel(String s);

    public String getId();

    public void setProperty(String property, Object value);

    public Object getProperty(String property);

    public Map<String,Object> getProperties();


}
