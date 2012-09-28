package edu.mit.cci.text.windowing;

import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 5/5/11
 * Time: 5:32 PM
 **/


/**
 * A "bin" is just a bin of objects in a temporal data stream.  Multiple bins may have at each window.
 *
 * Consider a matrix of data, where column index reflects temporal index, and rows represent parallel temporal processes.
 * Then, windows indicate columns, and bins indicate rows.
 *
 */
public interface BinningStrategy<T> {

    public Date[] getBinBoundaries(int i);
    public int findBin(Date d);
    public List<T> getBinContents(int i);
    public int getNumBins(int i);
    public boolean binContains(int idx, Date date);
    public boolean beforeBin(Date date, int idx);
    public boolean afterBin(Date date, int idx);

    public static interface DateInspector<T> {
        public Date getDate(T obj);

    }
}
