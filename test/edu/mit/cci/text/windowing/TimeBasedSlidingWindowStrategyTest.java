package edu.mit.cci.text.windowing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * TimeBasedSlidingWindowStrategy Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>09/25/2012</pre>
 */
public class TimeBasedSlidingWindowStrategyTest extends TestCase {
    public TimeBasedSlidingWindowStrategyTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Method: getNumberWindows()
     */
    public void testProcess() throws Exception {
        DateFormat f = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date[] dates = new Date[0];
        try {
            dates = new Date[]{
                    f.parse("10/1/97 12:00 AM"),
                    f.parse("10/1/97 12:10 AM"),
                    f.parse("10/1/97 12:30 AM"),
                    f.parse("10/1/97 12:40 AM"),
                    f.parse("10/1/97 1:50 AM"),
                    f.parse("10/1/97 2:30 AM")

            };
        } catch (ParseException p) {
            //ignore
        }
        List<Windowable> w = MockWindowable.getWindows(dates);
        TimeBasedSlidingWindowStrategy<Windowable> strat = new TimeBasedSlidingWindowStrategy<Windowable>(dates[0],dates[dates.length-1],30*60*1000,9*60*1000);
        int[][] expect = new int[][] {
                {0}, //12:00 - 12:09
                {0,1}, //12:00 - 12:18
                {0,1},  //12:00 - 12:27
                {1,2}, //12:06 - 12:36
                {2,3}, //12:15 - 12:45
                {2,3}, //12:24 - 12:54
                {3}, //12:33 - 1:03
                {}, //12:42 - 1:12
                {}, //12:51 - 1:21
                {},//1:00 - 1:30
                {},//1:09 - 1:39
                {},//1:18 - 1:48
                {4},//1:27 - 1:57
                {4},//1:36 - 2:06
                {4},// 1:47 - 2:17
                {},// 1:56 - 2:26
                {5},// 2:05 - 2:35


        };

        strat.setData(w);

        assertEquals("Incorrect number of windows",expect.length,strat.getNumberWindows());
        for (int i = 0;i<strat.getNumberWindows();i++) {
            List<Windowable> data =  strat.getWindow(i);
            assertEquals("Window "+i+" is not the right length",expect[i].length,data.size());
            for (int j = 0;j<data.size();j++) {
                assertSame("Element "+j+" in window "+i+" is not the same",w.get(expect[i][j]),data.get(j));
            }
        }



    }


    public static Test suite() {
        return new TestSuite(TimeBasedSlidingWindowStrategyTest.class);
    }
}
