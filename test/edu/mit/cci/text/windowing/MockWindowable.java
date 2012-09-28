package edu.mit.cci.text.windowing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/25/12
 * Time: 8:05 AM
 */
public class MockWindowable implements Windowable {

    private Date d;
    private int size;

    public MockWindowable(Date start, int size) {
        this.d = start;
        this.size = size;
    }

    public static List<Windowable> getWindows(int... sizes) {
        Windowable[] result = new Windowable[sizes.length];
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        for (int i=0;i<sizes.length ; i++) {
            result[i] = new MockWindowable(cal.getTime(), sizes[i]);
            cal.add(Calendar.MINUTE, 1);

        }

        return Arrays.asList(result);

    }

    public static List<Windowable> getWindows(Date... dates) {
         Windowable[] result = new Windowable[dates.length];
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        for (int i = 0;i<dates.length;i++) {
            result[i] = new MockWindowable(dates[i], 100);
            cal.add(Calendar.MINUTE, 1);

        }

        return Arrays.asList(result);

    }

    public Date getStart() {
        return d;
    }

    public Date getEnd() {
        return d;
    }

    public int getSize() {
        return size;
    }

    public String toString() {
        return "MockWindow:"+getStart()+":"+getSize();
    }
}
