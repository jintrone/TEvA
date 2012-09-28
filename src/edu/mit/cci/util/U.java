package edu.mit.cci.util;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * User: jintrone
 * Date: 9/24/12
 * Time: 11:34 PM
 */
public class U {

       public static <T, V> int binarySearch(List<? extends T> list, V key, Comparator<? super V> c, Adapter<? super T, V> adapter) {

        if (list instanceof RandomAccess || list.size() < 5000)
            return indexedBinarySearch(list, key, c, adapter);
        else
            return iteratorBinarySearch(list, key, c, adapter);
    }

    private static <T, V> int indexedBinarySearch(List<? extends T> l, V key, Comparator<? super V> c, Adapter<? super T, V> adapter) {
        int low = 0;
        int high = l.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = l.get(mid);
            int cmp = c.compare(adapter.adapt(midVal), key);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    private static <T, V> int iteratorBinarySearch(List<? extends T> l, V key, Comparator<? super V> c, Adapter<? super T, V> adapter) {
        int low = 0;
        int high = l.size() - 1;
        ListIterator<? extends T> i = l.listIterator();
        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = get(i, mid);
            int cmp = c.compare(adapter.adapt(midVal), key);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

     /**
     * 308        * Gets the ith element from the given list by repositioning the specified
     * 309        * list listIterator.
     * 310
     */
    private static <T> T get(ListIterator<? extends T> i, int index) {
        T obj = null;
        int pos = i.nextIndex();
        if (pos <= index) {
            do {
                obj = i.next();
            } while (pos++ < index);
        } else {
            do {
                obj = i.previous();
            } while (--pos > index);
        }
        return obj;
    }

      public static interface Adapter<T, V> {
        public V adapt(T obj);
    }
}
