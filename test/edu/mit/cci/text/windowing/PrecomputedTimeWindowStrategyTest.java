package edu.mit.cci.text.windowing;

import junit.framework.TestCase;

import java.util.Date;
import java.util.List;

/**
 * SizeBasedSlidingWindowingStrategy Tester.
 *
 * @author <Authors name>
 * @since <pre>09/25/2012</pre>
 * @version 1.0
 */
public class PrecomputedTimeWindowStrategyTest extends TestCase {
    public PrecomputedTimeWindowStrategyTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     *
     * Method: analyze()
     *
     */
    public void testSizedWindows() throws Exception {
        List<Windowable> w = MockWindowable.getWindows(100,20,30,70,140,140);
        Date[][] windows = WindowingUtils.analyzeBySize(w, 120, 50);
        PrecomputedTimeWindowStrategy<Windowable> wstrat = new PrecomputedTimeWindowStrategy<Windowable>(windows);
        int[][] expect = new int[][] {
                {0,1},  //120
                {1,2,3}, //120
                {3,4}, //210
                {4,5}, //280

        };

        wstrat.setData(w);
        assertEquals("Incorrect number of windows",expect.length,wstrat.getNumberWindows());
        for (int i = 0;i<wstrat.getNumberWindows();i++) {
            List<Windowable> data =  wstrat.getWindow(i);
            assertEquals("Window "+i+" is not the right length",expect[i].length,data.size());
            for (int j = 0;j<data.size();j++) {
                assertSame(w.get(expect[i][j]),data.get(j));
            }
        }

    }
}

