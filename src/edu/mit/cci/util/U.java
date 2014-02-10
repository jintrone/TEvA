package edu.mit.cci.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/24/12
 * Time: 11:34 PM
 */
public class U {

    //Collection methods

    public static <T> T last(List<T> l) {
        return (l==null || l.isEmpty())?null:l.get(l.size()-1);
    }

    public static <T> T first(List<T> l) {
        return (l==null || l.isEmpty())?null:l.get(0);
    }

    public static <T> T first(Collection<T> c) {
        return (c==null || c.isEmpty())?null:c.iterator().next();
    }

    //Slow!
    public static <T> T last(Collection<T> c) {
        return (c==null || c.isEmpty())?null:last(new ArrayList<>(c));
    }


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

    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        f.delete();
//        if (!f.delete())
//            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    public static void cleanDirectory(File f) throws IOException {
        if (!f.isDirectory()) return;
        else {
            for (File f2:f.listFiles()) {
                delete(f2);
            }
        }
    }

    public static interface Adapter<T, V> {
        public V adapt(T obj);
    }


    public static Map<String, Object> mapify(Object... s) {
        Map<String, Object> result = new HashMap();
        for (int i = 1; i < s.length; i += 2) {
            result.put(s[i - 1].toString(), s[i]);
        }
        return result;
    }

    public static <T> Iterable<T> multiIterator(final Iterable<T>... cs) {
        return new Iterable<T>() {


            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    List<Iterator<T>> its = new ArrayList<Iterator<T>>();

                    {
                        for (Iterable<T> i : cs) {
                            its.add(i.iterator());
                        }
                    }

                    public boolean hasNext() {
                        while (its.size() > 0 && !its.get(0).hasNext()) {
                            its.remove(0);

                        }
                        return its.size() > 0 && its.get(0).hasNext();
                    }

                    public T next() {
                        while (its.size() > 0 && !its.get(0).hasNext()) {
                            its.remove(0);

                        }
                        if (its.size() > 0) return its.get(0).next();
                        else throw new NoSuchElementException();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException("Remove is not supported");
                    }
                };
            }
        };
    }

    /**
     * Splits, just like the underlying String.split, but skips any empty tokens in the result
     *
     * @param str
     * @param regx
     * @return
     */
    public static String[] mysplit(String str, String regx) {
        String[] tmp = str.split(regx);
        List<String> result = new ArrayList<String>();
        for (String s : tmp) {
            if (!s.isEmpty()) {
                result.add(s);
            }
        }
        return result.toArray(new String[result.size()]);
    }


    public static File getAnyFileNative(String message, String file, int type) {
        //JFileChooser chooser = new JFileChooser(file);

        JFrame frame = new JFrame();
        frame.setVisible(true);
        FileDialog dialog = new FileDialog(frame);
        dialog.setTitle(message);
        dialog.setFile(file);
        dialog.setMode(type);
        dialog.setVisible(true);
        File result =  dialog.getFiles()[0];
        frame.dispose();
        return result;

//        chooser.setFileSelectionMode(type);
//        if (message != null && !message.isEmpty()) chooser.setDialogTitle(message);
//        int returnVal = chooser.showOpenDialog(frame);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File f = chooser.getSelectedFile();
//            frame.dispose();
//            return f;
//        } else return null;
    }

    public static File getAnyFile(String message, String file, int type) {
        //JFrame frame = new JFrame();
        JFileChooser chooser = new JFileChooser(file);
        chooser.setFileSelectionMode(type);
        if (message != null && !message.isEmpty()) chooser.setDialogTitle(message);
        int returnVal = chooser.showOpenDialog(null);
        File f = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();

        }
        //frame.dispose();
        return f;
    }

    public static boolean move(File f, File dest) {
        File actualdest;
        if (dest.isFile()) {
            actualdest = dest;
        } else {
            actualdest = new File(dest, f.getName());
        }
        return f.renameTo(actualdest);

    }

    public static <T> List<T> filter(List<T> filtered, Filter<T> f) {
        List<T> result = new ArrayList<T>();
        if (filtered != null && !filtered.isEmpty()) {
            for (T obj : filtered) {
                if (f.accept(obj)) {
                    result.add(obj);
                }
            }
        }
        return result;

    }

    public static <T> MappingIterator<T> map(Collection<T> x) {
        return new MappingIterator<T>(x);

    }

    public static class MappingIterator<T> {

        Collection<T> c;

        public MappingIterator(Collection<T> c) {
            this.c = c;
        }

        public void forEach(F<T> fx) {
            for (T a : c) {
                fx.apply(a);
            }
        }

    }


    public static <K,V> void lput (Map<K,List<V>> map, K key, V val) {
        List<V> l = map.get(key);
        if (l == null) {
            map.put(key, l= new ArrayList<V>());
        }
        l.add(val);

    }


}
