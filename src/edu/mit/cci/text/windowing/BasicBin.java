package edu.mit.cci.text.windowing;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: jintrone
 * Date: 4/18/13
 * Time: 1:00 PM
 */
public class BasicBin<T> extends ArrayList<T> implements Bin<T> {

    int firstIndex = 0;

    public BasicBin() {
    }

    public BasicBin(Collection<? extends T> ts) {
        super(ts);
    }

    public BasicBin(int idx, Collection<? extends T> ts) {
        super(ts);
        firstIndex = idx;
    }

    public BasicBin(int i) {
        super(i);
    }

    public void setFirstItemIndex(int idx) {
        this.firstIndex = idx;
    }

    public int getFirstItemIndex() {
        return firstIndex;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        String sep = "";
        for (int i=0;i<this.size();i++) {

            builder.append(sep);

            if (i == getFirstItemIndex()) {
                builder.append("*");
            }
            builder.append(this.get(i));
            sep = ", ";

        }
        builder.append("]");
        return builder.toString();
    }

}
