package edu.mit.cci.teva.cpm.cos;

import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.engine.TevaParameters;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * User: jintrone
 * Date: 11/16/12
 * Time: 4:38 PM
 */
public class CosCommunityFinderTest extends TestCase {

    public void testCommunityFinder() throws IOException, CommunityFinderException {
        TevaParameters parameters = new TevaParameters();
        parameters.setWorkingDirectory("sample/COSUnitTest/output/");
        CosCommunityFinder finder = new CosCommunityFinder(true,true,parameters);
        finder.findCommunities(new File("sample/COSUnitTest/graph.txt"),parameters.getFixedCliqueSize(),0);



    }

}
