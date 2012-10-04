package edu.mit.cci.text.windowing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/29/12
 * Time: 10:32 AM
 */
public class WindowingUtils {


    public static <T extends Windowable> Date[][] analyzeBySize(List<T> current, int numTokensWidth, int numTokensDelta) {
        List<DateWidth> buffer = new ArrayList<DateWidth>();
        List<Date[]> windows = new ArrayList<Date[]>();
        int wcount = 0;
        Iterator<T> it = current.iterator();
        while (it.hasNext()) {
            T next = it.next();
            DateWidth dw = new DateWidth(next.getStart(), next.getTokens().size());
            buffer.add(dw);

            wcount += dw.width;

            if (wcount >= numTokensWidth || !it.hasNext()) {
                Date[] d = new Date[]{buffer.get(0).date, buffer.get(buffer.size() - 1).date};
                windows.add(d);
                int temp = 0;
                while (temp < numTokensDelta) {
                    int size = buffer.remove(0).width;
                    temp += size;


                }
                wcount -= temp;


            }


        }
        return windows.toArray(new Date[windows.size()][]);

    }

    static class DateWidth {
        Date date;
        int width;

        public DateWidth(Date date, int width) {
            this.date = date;
            this.width = width;
        }

    }
}
