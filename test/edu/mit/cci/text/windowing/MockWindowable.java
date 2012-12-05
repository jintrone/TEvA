package edu.mit.cci.text.windowing;

import java.util.ArrayList;
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

    private List<String> data;



    public MockWindowable(Date start, String... tokens) {
        this.d = start;
        this.data = Arrays.asList(tokens);
    }

    public MockWindowable(Date start, int size) {
        this.d = start;
        this.data = genTokens(0, size - 1);
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

    public static List<Windowable> getWindows(Date start, String[]... input) {
       Windowable[] result = new Windowable[input.length];
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(start);
        for (int i=0;i<input.length ; i++) {
            result[i] = new MockWindowable(cal.getTime(), input[i]);
            cal.add(Calendar.MINUTE, 1);

        }

        return Arrays.asList(result);
    }

    public static List<Windowable> getWindows(Date... dates) {
        Windowable[] result = new Windowable[dates.length];
        for (int i = 0;i<dates.length;i++) {
            result[i] = new MockWindowable(dates[i], 100);


        }

        return Arrays.asList(result);

    }

    public static List<String> genTokens(int low, int high) {
       List<String> result = new ArrayList<String>();
        for (int i=low;i<=high;i++) {
            result.add(i+"");
        }
        return result;
    }

    public Date getStart() {
        return d;
    }

    public List<String> getTokens() {
        return data;
    }

    public String getRawData() {
        return data.toString();
    }

    public String getId() {
        return hashCode()+"";
    }




    public String toString() {
        return "MockWindow:"+getStart()+":"+data.size();
    }


}
