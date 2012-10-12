package edu.mit.cci.text.windowing;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Slides a window of some width at a given delta across a sample. Guarantees that data at
 * the max time will be included in a window.  Windows are inclusive of the start,
 * exclusive of their end
 *
 * User: jintrone
 * Date: 5/23/11
 * Time: 6:18 PM
 */
public class TimeBasedSlidingWindowStrategy<T extends Windowable> implements WindowStrategy<Windowable> {

    int numWindows = 1;
    List<Windowable> current;
    Date min, max;
    long size;
    long delta;

    Logger log = Logger.getLogger(TimeBasedSlidingWindowStrategy.class);


    public TimeBasedSlidingWindowStrategy(Date min, Date max, long size, long delta) {
        //this is so embarrassing
        long x = min.getTime();
        while (x+size <= max.getTime()) {
            numWindows++;
            x+=delta;
        }
        this.min = min;
        this.max = max;
        this.size = size;
        this.delta = delta;
    }


    public int getNumberWindows() {
        return numWindows;
    }

    public void setData(List<Windowable> args) {
        this.current = args;
    }

    public Date[] getWindowBounds(int idx) {
       Date start = new Date(min.getTime() + (idx * delta));
       return new Date[] {start,new Date(start.getTime()+size)};

    }

    public List<Windowable> getWindow(int idx) {
        Date beginning = getWindowBounds(idx)[0];
        Date end = getWindowBounds(idx)[1];


        int first = -1;
        int last = -1;
        if (current.get(0).getStart().after(end) || current.get(current.size() - 1).getStart().before(beginning))
            return Collections.emptyList();
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getStart().before(beginning)) continue;
            else if (first < 0) {
                first = i;
            }
            if (current.get(i).getStart().equals(end) || current.get(i).getStart().after(end)) {
                last = i;
                break;
            }


        }
        if (first < 0) return Collections.emptyList();
        if (last < 0) {
            last = current.size();
        }


        List<Windowable> result = current.subList(first, last);

        return result;
    }

    public Date[][] getWindowBoundaries() {
       List<Date[]> dates = new ArrayList<Date[]>();
        for (int i = 0;i<getNumberWindows();i++) {
            dates.add(getWindowBounds(i));
        }
        return dates.toArray(new Date[dates.size()][]);

    }


}
