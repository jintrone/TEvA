package edu.mit.cci.teva;

import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.engine.NetworkProvider;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 6/3/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TevaRunner {
    NetworkProvider getNetworkProvider() throws IOException;

    void processAndSave() throws IOException, CommunityFinderException, JAXBException;
}
