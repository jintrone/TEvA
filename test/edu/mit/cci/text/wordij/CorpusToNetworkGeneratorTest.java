package edu.mit.cci.text.wordij;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.text.windowing.BasicBinningStrategy;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.MockWindowable;
import edu.mit.cci.text.windowing.TimeBasedSlidingWindowStrategy;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * CorpusToNetworkGenerator Tester.
 *
 * @author <Authors name>
 * @since <pre>09/28/2012</pre>
 * @version 1.0
 */
public class CorpusToNetworkGeneratorTest extends TestCase {

    private static Logger log = Logger.getLogger(CorpusToNetworkGeneratorTest.class);

    public CorpusToNetworkGeneratorTest(String name) {
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
    public void testProcessing() throws Exception {
        Calendar c = new GregorianCalendar(2000,1,1,9,0);
        final Date start = c.getTime();
        List<Windowable>  bin1 = MockWindowable.getWindows(c.getTime(),
                new String[]{"1", "2", "3", "4", "5"},  //9:00
                new String[]{"3", "4", "5", "6", "7"},  //9:01
                new String[]{"5", "6", "7", "8", "9"}); //9:02
        c.add(Calendar.MINUTE,1);
        List<Windowable>  bin2 = MockWindowable.getWindows(c.getTime(),
                       new String[]{"1","2","3","4","5"}, //9:01
                new String[]{"3","4","5","6","7"},        //9:02
                new String[]{"5","6","7","8","9"});       //9:03
        c.add(Calendar.MINUTE,1);
        final List<Windowable>  bin3 = MockWindowable.getWindows(c.getTime(),
                             new String[]{"1","2","3","4","5"}, //9:02
                new String[]{"3","4","5","6","7"},              //9:03
                new String[]{"5","6","7","8","9"});             //9:04

        List<List<Windowable>> corpus = new ArrayList<List<Windowable>>();
        corpus.add(bin1);
        corpus.add(bin2);
        corpus.add(bin3);

        log.info("From:"+start+" - "+bin3.get(bin3.size()-1).getStart());

        WindowStrategy.Factory<Windowable> factory = new WindowStrategy.Factory<Windowable>() {
            public WindowStrategy<Windowable> getStrategy() {
                return new TimeBasedSlidingWindowStrategy<Windowable>(start,bin3.get(bin3.size()-1).getStart(),1000*60*2,1000*60);
            }
        };
        BinningStrategy<Windowable> bins = new BasicBinningStrategy<Windowable>(corpus,factory);
        assertEquals("Incorrect number of bins",3,bins.getNumBins());
        assertEquals("Incorrect number of windows",4,bins.getNumWindows());

        for (WindowStrategy<Windowable> bs:((BasicBinningStrategy<Windowable>)bins).binmodel) {
            for (int i=0;i<bs.getNumberWindows();i++) {
                Date[] bounds = ((TimeBasedSlidingWindowStrategy<Windowable>)bs).getWindowBounds(i);
                log.debug("Window bounds: "+bounds[0]+","+bounds[1]);
            }
        }


        int[] expect = new int[] {2,3,3,2};

        for (int win = 0; win < bins.getNumWindows(); win++) {
            UndirectedJungNetwork graph = new UndirectedJungNetwork();
            List<List<Windowable>> b = bins.getDataAtWindow(win);
            int num = expect[win];
            for (List<Windowable> w:b) {
                log.debug("Processing "+w);
                if (!w.isEmpty()) {
                    num--;
                }
            }
            assertEquals("Did not identify enough component networks in window "+win,0,num);
        }


        CorpusToNetworkGenerator<Windowable> generator = new CorpusToNetworkGenerator<Windowable>(bins,new LinearWeightNetworkGenerator(3,2));
        List<Network> result = generator.analyze();
        assertEquals("Incorrect number of networks generated",4,result.size());



    }




    public static Test suite() {
        return new TestSuite(CorpusToNetworkGeneratorTest.class);
    }
}
