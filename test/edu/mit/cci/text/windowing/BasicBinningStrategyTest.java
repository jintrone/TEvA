package edu.mit.cci.text.windowing;

import com.sun.xml.internal.rngom.digested.DAttributePattern;
import edu.mit.cci.sna.Network;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * BasicBinningStrategy Tester.
 *
 * @author <Authors name>
 * @since <pre>09/28/2012</pre>
 * @version 1.0
 */
public class BasicBinningStrategyTest extends TestCase {

    private static Logger log = Logger.getLogger(BasicBinningStrategy.class);

    public BasicBinningStrategyTest(String name) {
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
     * Method: getBinContents(int i)
     *
     */
    public void testProcess() throws Exception {
        Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        List<Date> dates = new ArrayList<Date>();
        for (int i = 0;i<10;i++) {
           dates.add(c.getTime());
           c.add(Calendar.HOUR,1);

        }
        log.info("Dates are "+dates);

        List<Windowable> one = MockWindowable.getWindows(dates.get(0),dates.get(1),dates.get(2),dates.get(3),dates.get(4));
        List<Windowable> two = MockWindowable.getWindows(dates.get(3),dates.get(4),dates.get(5),dates.get(6));
        List<Windowable> three = MockWindowable.getWindows(dates.get(4),dates.get(6),dates.get(7),dates.get(8));

        Collections.shuffle(one);
        Collections.shuffle(two);
        Collections.shuffle(three);

        final Date[][] windows = new Date[9][];
        for (int i =0;i<9;i++) {
            windows[i] = new Date[]{dates.get(i),new Date(dates.get(i).getTime()+1000)};
        }
        WindowStrategy.Factory<Windowable> factory = new WindowStrategy.Factory<Windowable>() {
            public WindowStrategy<Windowable> getStrategy() {
                return new PrecomputedTimeWindowStrategy<Windowable>(windows);
            }
        };
        BinningStrategy<Windowable> s = new BasicBinningStrategy<Windowable>(Arrays.asList(three,two,one),factory);

        assertEquals(3,s.getNumBins());
        assertEquals(9,s.getNumWindows());
        int[] expectedbincounts = new int[]{1,1,1,2,3,1,2,1,1};

        for (int i = 0;i<9;i++) {
            int count = 0;
            log.info("Checking window from "+windows[i][0]+" to "+windows[i][1]);
            List<List<Windowable>> data =  s.getDataAtWindow(i);

            for (List<Windowable> d:data) {
                if (!d.isEmpty()) count++;
                log.info("Found data: "+d);
            }
            assertEquals("Data count in window "+i+" is wrong",expectedbincounts[i],count);


        }


    }




    public static Test suite() {
        return new TestSuite(BasicBinningStrategyTest.class);
    }
}
