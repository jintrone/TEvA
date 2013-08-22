package edu.mit.cci.text.windowing;

import java.util.List;

/**
 * User: jintrone
 * Date: 5/5/11
 * Time: 5:32 PM
 **/


/**
 * A "bin" is just a bin of objects in a temporal data stream.  Multiple bins may exist at each window.
 *
 * A bin has no specific clas; in this API, it is merely a collection
 *
 * Consider a matrix of data, where column index reflects temporal index, and rows represent parallel temporal processes.
 * Then, windows indicate columns, and bins indicate rows.
 *
 */
public interface BinningStrategy<T> {

    public List<Bin<T>> getDataAtWindow(int window);
    public int getNumWindows();

}
