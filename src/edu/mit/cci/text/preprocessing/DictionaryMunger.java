package edu.mit.cci.text.preprocessing;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The dictionary munger also takes an input matrix of the form
 * <p/>
 * token1 token2 token3 replacement
 * token1 token2 replacement
 * token4 token5 token6 replacement
 * <p/>
 * It is an aggressive matcher, seeking to replace the greatest number of words
 * <p/>
 * User: jintrone
 * Date: 9/29/11
 * Time: 5:08 PM
 */


//TODO refactor this class into a module rather than subclass and finish implementing compositemunger
public class DictionaryMunger implements Munger {
    private static int pathid = 0;
    private static Logger log = Logger.getLogger(DictionaryMunger.class);

    protected Node top = new Node("TOP");
    private List<String> wordbuffer = new ArrayList<String>();
    private List<String> buffer = new ArrayList<String>();
    private Map<Node, Integer> rowmap = new HashMap<Node, Integer>();
    protected SimpleMatrix<Path> matrix;






    public DictionaryMunger(String[][] replacements) {

        for (String[] r : replacements) {
            top.add(r);
        }

        matrix = new SimpleMatrix<Path>(count(top, 0), 0);
    }

    public static DictionaryMunger read(InputStream i) throws IOException {
        List<String[]> dictionary = new ArrayList<String[]>();
        BufferedReader stream = new BufferedReader(new InputStreamReader(i));
        String line = null;
        while ((line= stream.readLine())!=null) {
            dictionary.add(line.trim().split("\\s+"));
        }
        return new DictionaryMunger(dictionary.toArray(new String[dictionary.size()][]));
    }


    protected int count(Node n, int depth) {
        rowmap.put(n, rowmap.size());
        if (depth == 1) n.isRoot = true;
        int result = 0;
        if (!n.children.isEmpty()) {
            for (Node child : n.children.values()) {
                result += count(child, depth + 1);
            }
        }
        return result + 1;
    }

    public boolean read(String input) {


        buffer.add(input);
        log.debug("Begin State: " + matrix.toString());
        log.debug("Adding: " + input);
        if (buffer.isEmpty()) {
            return false;
        } else {

            wordbuffer.addAll(buffer);
            matrix.addColumns(buffer.size());
            buffer.clear();

        }


        Node topmatch = top.match(input);
        if (topmatch != null) {
            matrix.set(rowmap.get(topmatch), matrix.cols() - 1, new Path(topmatch));
        }

        int i = 0;
        boolean result = false;
        while (!wordbuffer.isEmpty() && i < wordbuffer.size() - 1) {
            boolean empty = true;
            for (Node n : rowmap.keySet()) {

                Path root = matrix.get(rowmap.get(n), i);
                if (root == null) continue;
                empty = false;
                if (n.isRoot()) {
                    if (root.isFrozen() || !root.extend(input, i)) {
                        log.debug("Can't extend " + root + ":" + root.nodes);
                        buffer.addAll(resolve(root, 0));
                        result = true;
                        break;
                        //hmm
                    }
                } else if (!root.extend(input, i)) {
                    root.prune(i);
                    root.freeze();
                }
            }
            if (empty) {
                buffer.add(wordbuffer.get(i));
                wordbuffer.remove(i);
                matrix.removeColumn(i);
                result = true;
            }
            if (!result) i++;
        }


        log.debug("End State: " + matrix.toString());

        return !buffer.isEmpty();
    }

    public List<String> flush() {
        List<String> result = new ArrayList<String>();
        if (!buffer.isEmpty()) {
            result.addAll(buffer);
            buffer.clear();
        }
        return result;
    }

    public List<String> finish() {
        log.debug("Finishing with wordbuffer: "+wordbuffer);
        List<String> result = new ArrayList<String>();
        while (!wordbuffer.isEmpty()) {
            boolean found = false;
            for (Node n : rowmap.keySet()) {
                if (matrix.cols() == 0) {
                    log.warn("Not enough columns in matrix!");
                }
                Path p = matrix.column(0).get(rowmap.get(n));
                if (p != null && n.isRoot()) {
                    found = true;
                    result.addAll(resolve(p, 0));
                    break;

                }
            }
            if (!found) {
                result.add(wordbuffer.get(0));
                wordbuffer.remove(0);
                matrix.removeColumn(0);
            }
        }

        return result;

    }




    private void removeColumn(int i) {
        for (Path p : matrix.column(i)) {
            if (p != null) {
                p.clear(i);
            }
        }
        matrix.removeColumn(i);
    }

    private List<String> resolve(Path p, int idx) {
        List<String> result = new ArrayList<String>();
        p.prune(idx);
        if (p.nodes.isEmpty()) {
            result.add(wordbuffer.get(idx));
            wordbuffer.remove(0);
            removeColumn(0);
        } else {
            result.add(p.last().replacement);
            for (int i = 0; i < p.nodes.size(); i++) {
                wordbuffer.remove(0);
                removeColumn(0);
            }
        }

        return result;


    }


    protected class Path {

        private boolean frozen = false;

        private List<Node> nodes = new ArrayList<Node>();


        private int id = pathid++;

        public Path(Node... nodes) {
            add(nodes);
        }

        public void freeze() {
            frozen = true;
        }

        public boolean isFrozen() {
            return frozen;
        }


        public boolean extend(String text, int i) {
            if (last() == null) {
                log.debug("Path is empty!");
            }
            Node n = last().match(text);
            if (n != null) {
                nodes.add(n);
                matrix.set(rowmap.get(n), i + nodes.size() - 1, this);
                return true;
            }
            return false;
        }

        public Node last() {
            return nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
        }

        public void add(Node... nodes) {
            this.nodes.addAll(Arrays.asList(nodes));
        }

        public void prune(int start) {

            while (last() != null && !last().hasReplacement()) {
                log.debug("Pruning");
                matrix.clear(rowmap.get(last()), start + nodes.size() - 1);
                nodes.remove(nodes.size() - 1);


            }
        }

        public void clear(int i) {
            for (Node n : nodes) {
                matrix.clear(rowmap.get(n), i++);
            }

        }

        public String toString() {
            return "[" + id + "]";
        }


    }

    public class Node {

        public Map<String, Node> children = new HashMap<String, Node>();

        private String me;

        public boolean isRoot = false;

        public Node(String me, String... remainder) {
            this.me = me;
            if (remainder != null && remainder.length > 0) add(remainder);
        }

        public String replacement = null;

        public void add(String... others) {
            log.debug("Process rule:" + Arrays.toString(others));
            if (others.length > 1) {
                Node next = children.get(others[0]);
                if (next == null) {
                    next = new Node(others[0], Arrays.copyOfRange(others, 1, others.length));
                    children.put(others[0], next);
                } else {
                    next.add(Arrays.copyOfRange(others, 1, others.length));
                }
            } else {
                log.debug("Adding replacement " + others[0]);
                replacement = others[0];
            }
        }

        public boolean hasReplacement() {
            return replacement != null;
        }

        public boolean isTail() {
            return children.isEmpty();
        }

        public Node match(String text) {
            return children.get(text);
        }

        public String getReplacement() {
            return replacement;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public String toString() {
            return me + (replacement != null ? "(" + replacement + ")" : "");
        }


    }

    protected class SimpleMatrix<T> {
        public List<List<T>> data = new ArrayList<List<T>>();

        private int rows = 0;

        public SimpleMatrix(int rows, int cols) {
            this.rows = rows;
            for (int i = 0; i < cols; i++) {

                data.add(emptyColumn());
            }
        }

        public int cols() {
            return data.size();
        }

        public int rows() {
            return rows;
        }

        public T get(int row, int col) {
            return data.get(col).get(row);
        }

        public void set(int row, int col, T val) {
            data.get(col).set(row, val);
        }

        public void clear(int row, int col) {
            set(row, col, null);
        }

        public void removeColumn(int col) {
            data.remove(col);
        }

        public void addColumns(int count) {
            for (int i = 0; i < count; i++) {
                data.add(emptyColumn());
            }
        }

        public List<T> column(int i) {
            return data.get(i);
        }

        private List<T> emptyColumn() {
            List<T> row = new ArrayList<T>();
            for (int j = 0; j < rows; j++) {
                row.add(null);
            }
            return row;
        }

        public String toString() {
            String header = "     ";
            for (String word : wordbuffer) {
                header += String.format("%-5s", word);
            }
            String row = "";
            for (Map.Entry<Node, Integer> ent : rowmap.entrySet()) {
                row += String.format("%-5s", ent.getKey());
                for (List<T> col : data) {
                    row += String.format("%-5s", col.get(ent.getValue()));
                }
                row += "\n";

            }
            return "\n" + header + "\n" + row;
        }


    }
}
