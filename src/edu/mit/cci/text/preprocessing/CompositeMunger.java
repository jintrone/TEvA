package edu.mit.cci.text.preprocessing;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/19/11
 * Time: 5:06 PM
 * <p/>
 * //TODO CLASS IS INCOMPLETE - will fail to flush down the whole chain if less than n-1 mungers have fired by last input
 */
public class CompositeMunger implements Munger {


    public Munger[] mungers;

    Logger log = Logger.getLogger(CompositeMunger.class);

    public CompositeMunger(Munger... mungers) {
        this.mungers = mungers;
    }


    public boolean read(String from) {

        List<String> input = new ArrayList<String>();
        input.add(from);
        return _munge(input, 0);
    }

    public boolean _munge(List<String> words, int munger) {
        log.debug("Munging "+words+" at level "+munger);
        Munger current = mungers[munger];
        boolean result = false;
        while (!words.isEmpty()) {
            String next = words.remove(0);
            if (current.read(next)) {
                if (munger < mungers.length -1) {
                    result = _munge(current.flush(),munger+1);
                }
            } else {
                result = false;
            }
        }
        return result;


    }

    public List<String> flush() {
        return mungers[mungers.length-1].flush();
    }

    public List<String> finish() {
        List<String> result = new ArrayList<String>();
        for (Munger m:mungers) {
            List<String> tmp = new ArrayList<String>();
            if (!result.isEmpty()) {
                for (String r:result) {
                    m.read(r);
                }
                tmp.addAll(m.flush());
            }
            tmp.addAll(m.finish());
            result.clear();
            result.addAll(tmp);
        }
        return result;
    }
}
