package edu.mit.cci.text.windowing;

import java.util.Collection;
import java.util.List;

/**
 * A bin is a simple wrapper around a list that provides a hint (the "first item index") to the network
 * generator to indicate which element is the first underlying item to be processed.  The preceding items
 * will be used to generate initial connections.
 *
 * User: jintrone
 * Date: 4/18/13
 * Time: 9:34 AM
 */
public interface Bin<T> extends List<T> {

    public int getFirstItemIndex();
    public void setFirstItemIndex(int i);

}
