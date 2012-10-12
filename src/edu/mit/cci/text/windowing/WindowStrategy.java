package edu.mit.cci.text.windowing;

import java.util.Date;
import java.util.List;

/**
 * A window delineates a contiguous area in a temporal data stream. WindowingStrategy provides
 * random access to an underlying windowed data stream
 *
 * User: jintrone
 * Date: 5/11/11
 * Time: 5:26 PM
 */
public interface WindowStrategy<T> {

    public int getNumberWindows();
    public List<T> getWindow(int i);
    public Date[][] getWindowBoundaries();

    /**
     * Sets the underlying data stream to be indexed.  Expects data to be sorted.
     * @param data
     */
    public void setData(List<T> data);

    public static interface Factory<T> {

        public WindowStrategy<T> getStrategy();

    }

}
