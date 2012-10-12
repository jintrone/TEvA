package edu.mit.cci.teva.serialization;

import edu.mit.cci.teva.engine.CommunityModel;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * User: jintrone
 * Date: 10/8/12
 * Time: 11:05 PM
 */
public class ConnectionMapJaxb extends XmlAdapter<ConnectionMapJaxb,Map<Integer,CommunityModel.Connection>> {



    @Override
    public Map<Integer, CommunityModel.Connection> unmarshal(ConnectionMapJaxb connectionMapJaxb) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ConnectionMapJaxb marshal(Map<Integer, CommunityModel.Connection> integerConnectionMap) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



}
