package edu.mit.cci.text.windowing;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Slides a window of some width at a given delta across a sample.  Slides the leading edge from
 * the start of the sample, updates the trailing edge.  Thus, the sliding window "fades into" the sample.
 *
 * User: jintrone
 * Date: 5/23/11
 * Time: 6:18 PM
 */
public class TimeBasedSlidingWindowStrategy<T extends Windowable> implements WindowStrategy {

    int numWindows = 0;
    List<T> current;
    Date min, max;
    long size;
    long delta;

    Logger log = Logger.getLogger(TimeBasedSlidingWindowStrategy.class);


    public TimeBasedSlidingWindowStrategy(Date min, Date max, long size, long delta) {
        numWindows = (int) Math.ceil((max.getTime()  - min.getTime()) / (float)delta);
        this.min = min;
        this.max = max;
        this.size = size;
        this.delta = delta;
    }


    public int getNumberWindows() {
        return numWindows;
    }

    public void setData(List args) {
        this.current = args;
    }

    public Date[] getWindowBounds(int idx) {
       Date end = new Date(min.getTime() + ((idx + 1) * delta));
       return new Date[] {new Date(Math.max(min.getTime(), end.getTime() - size)),end};

    }

    public boolean overlaps(int myindx, TimeBasedSlidingWindowStrategy strategy, int otheridx) {
      Date[] other = strategy.getWindowBounds(otheridx);
      Date[] mine = this.getWindowBounds(myindx);
      return mine[0].compareTo(other[1]) < 1 && mine[1].compareTo(other[0]) > -1;

    }

    public List<T> getWindow(int idx) {
        Date beginning = getWindowBounds(idx)[0];
        Date end = getWindowBounds(idx)[1];
        log.debug("Window " + idx + " from " + beginning + " to " + end);


        int first = -1;
        int last = -1;
        if (current.get(0).getStart().after(end) || current.get(current.size() - 1).getStart().before(beginning))
            return Collections.emptyList();
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getStart().before(beginning)) continue;
            else if (first < 0) {
                first = i;
            }
            if (current.get(i).getStart().after(end)) {
                last = i;
                break;
            }


        }
        if (first < 0) return Collections.emptyList();
        if (last < 0) {
            last = current.size();
        }


        List<T> result = current.subList(first, last);
        if (log.getEffectiveLevel().equals(Level.DEBUG)) {

            StringBuilder builder = new StringBuilder();
            for (T p : result) {

                builder.append(p.getStart()).append(";");
            }
            log.debug("Process posts " + builder.toString());
        }
        return result;
    }
}
