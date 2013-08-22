package edu.mit.cci.text.windowing;

import edu.mit.cci.teva.model.MockPost;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.preprocessing.AlphaNumericTokenizer;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import sun.util.calendar.LocalGregorianCalendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 4/22/13
 * Time: 11:05 AM
 */
public class ReplyBasedBinningStrategyTest extends TestCase {

    private static Logger log = Logger.getLogger(ReplyBasedBinningStrategyTest.class);

    public List<WindowablePostAdapter> data;

    public Date[] dates = new Date[] {
            new GregorianCalendar(2000,1,1,9,0).getTime(),
            new GregorianCalendar(2000,1,1,9,1).getTime(),
            new GregorianCalendar(2000,1,1,9,2).getTime(),
            new GregorianCalendar(2000,1,1,9,3).getTime(),
            new GregorianCalendar(2000,1,1,9,4).getTime(),
            new GregorianCalendar(2000,1,1,9,5).getTime(),
            new GregorianCalendar(2000,1,1,9,6).getTime(),
            new GregorianCalendar(2000,1,1,9,7).getTime(),
            new GregorianCalendar(2000,1,1,9,8).getTime(),
            new GregorianCalendar(2000,1,1,9,9).getTime()
    };

    public int[][] lineage = new int[][] {
            {1,0},
            {2,0},
            {3,0},
            {4,2},
            {5,4},
            {6,5},
            {7,3},
            {8,6},
            {9,0}
    };


    public void setUp() throws Exception {
        super.setUp();
        Map<Integer,Integer> fromTo = new HashMap<Integer,Integer>();
        for (int[] reply:lineage) {
          fromTo.put(reply[0],reply[1]);
        }
        data = new ArrayList<WindowablePostAdapter>();
        for (int i  =0;i<dates.length;i++) {
            Post p = new MockPost("0",fromTo.containsKey(i)?data.get(fromTo.get(i)).getPost():null,dates[i]);
            data.add(new WindowablePostAdapter(p,new AlphaNumericTokenizer()));
        }
    }

    public void testProcess() throws Exception {
        WindowStrategy.Factory<Windowable> fact = new WindowStrategy.Factory<Windowable>() {
            public WindowStrategy<Windowable> getStrategy() {
                return new TimeBasedSlidingWindowStrategy (dates[0],dates[9],60*1000*5,60*1000);
            }
        };
        TimeBasedSlidingWindowStrategy s = new TimeBasedSlidingWindowStrategy(dates[0],dates[9],60*1000*3,60*1000);
        ReplyBasedBinningStrategy strategy = new ReplyBasedBinningStrategy(Collections.singletonList(data),fact);
        for (int i = 0;i<strategy.getNumWindows();i++) {
            log.debug(i+". "+strategy.getDataAtWindow(i));
        }





    }

}
