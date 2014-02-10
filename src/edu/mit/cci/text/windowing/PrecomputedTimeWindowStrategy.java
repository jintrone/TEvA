package edu.mit.cci.text.windowing;

import edu.mit.cci.util.U;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/28/12
 * Time: 9:55 PM
 */
public class PrecomputedTimeWindowStrategy implements WindowStrategy<Windowable> {

    Date[][] windows;
    List<? extends Windowable> data;

    public PrecomputedTimeWindowStrategy(Date[]... windows) {
        this.windows = windows;
    }

    public int getNumberWindows() {
        return windows.length;
    }

    public List<Windowable> getWindow(int i) {
        int start = U.binarySearch(data, windows[i][0], new Comparator<Date>() {

                    public int compare(Date post, Date post1) {
                        return post.compareTo(post1);
                    }
                }, new U.Adapter<Windowable, Date>() {

            public Date adapt(Windowable obj) {
                return obj.getStart();
            }
        });
        if (start < 0) {
            start = -1 * (++start);
        }
        if (start == data.size()) {
            return Collections.emptyList();
        } else {
            int end = start;
            while (end < data.size() && data.get(end).getStart().compareTo(windows[i][1]) <= 0) {
                end++;
            }
            return (List<Windowable>) data.subList(start, end);
        }


    }

    public Date[][] getWindowBoundaries() {
        return windows;
    }



    public void setData(List<? extends Windowable> data) {
        this.data = data;
    }


}
