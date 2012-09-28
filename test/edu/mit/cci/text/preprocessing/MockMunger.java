package edu.mit.cci.text.preprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/23/12
 * Time: 10:05 PM
 */
public class MockMunger implements Munger {

    List<String> buffer = new ArrayList<String>();
    private int delay = 0;

    public MockMunger(int delay) {
        this.delay = delay;
    }

    public boolean read(String from) {
        buffer.add(from);
        if (buffer.size() < delay) {
            return false;
        } else return true;
    }

    public List<String> flush() {
        List<String> result = new ArrayList<String>(buffer);
        buffer.clear();
        return result;
    }

    public List<String> finish() {
        return buffer;
    }
}
