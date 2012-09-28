package edu.mit.cci.sna;

import java.util.Map;

/**
 * Simple interface around a generic edge
 *
 * User: jintrone
 * Date: 9/21/12
 * Time: 3:54 PM
 */
public interface Edge {

    public Node[] getEndpoints();

    public boolean isDirected();

    public float getWeight();

    public void setWeight(float weight);

    public void setProperty(String property, Object value);

    public Object getProperty(String property);

    public Map<String,Object> getProperties();
}
