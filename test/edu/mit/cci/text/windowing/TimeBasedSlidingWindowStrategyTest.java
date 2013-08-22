package edu.mit.cci.text.windowing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TimeBasedSlidingWindowStrategy Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>09/25/2012</pre>
 */
public class TimeBasedSlidingWindowStrategyTest extends TestCase {
    private static Logger log = Logger.getLogger(TimeBasedSlidingWindowStrategy.class );

    public TimeBasedSlidingWindowStrategyTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testWindowsOnEdge() throws Exception {
        Date start = new GregorianCalendar(2000,1,1,9,0).getTime();
        Date end = new GregorianCalendar(2000,1,1,9,3).getTime();
        TimeBasedSlidingWindowStrategy ts = new TimeBasedSlidingWindowStrategy(start,end,60*1000,60*1000);
        assertEquals("Incorrect number of windows", 4, ts.getNumberWindows());

    }


    public void testWindows1() throws Exception {
        Date start = new GregorianCalendar(2000,1,1,9,0).getTime();
        Date end = new GregorianCalendar(2000,1,1,9,3).getTime();

        TimeBasedSlidingWindowStrategy ts = new TimeBasedSlidingWindowStrategy(start,end,2*60*1000,60*1000);
        assertEquals("Incorrect number of windows", 3, ts.getNumberWindows());


    }

    public void testWindows() throws Exception {
        Date start = new GregorianCalendar(2000,1,1,9,0).getTime();
        Date end = new GregorianCalendar(2000,1,1,9,4).getTime();

        TimeBasedSlidingWindowStrategy ts = new TimeBasedSlidingWindowStrategy(start,end,2*60*1000,60*1000);
        assertEquals("Incorrect number of windows", 4, ts.getNumberWindows());




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
        TimeBasedSlidingWindowStrategy strat = new TimeBasedSlidingWindowStrategy(dates[0],dates[dates.length-1],30*60*1000,9*60*1000);
        int[][] expect = new int[][] {
                {0,1}, //12:00 - 12:30
                {1,2}, //12:09 - 12:39
                {2,3},  //12:18 - 12:48
                {2,3}, //12:27 - 12:57
                {3}, //12:36 - 1:06
                {}, //12:45 - 1:15
                {}, //12:54 - 13:24
                {}, //13:03 - 13:33
                {}, //13:12 - 13:42
                {4},//13:21 - 13:51
                {4},//13:30 - 14:00
                {4},//13:39 - 14:09
                {4},//13:48 - 14:18
                {},//13:57 - 14:27
                {5} // 14:06 - 14:36



        };

        for (int i =0;i<strat.getNumberWindows();i++) {
            log.debug("Windows: "+strat.getWindowBounds(i)[0]+strat.getWindowBounds(i)[1]);
        }

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
