package edu.mit.cci.text.windowing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 5/23/11
 * Time: 5:33 PM
 */
public class SingleThreadBinningStrategy implements BinningStrategy<Windowable> {


    WindowStrategy<Windowable> windowStrategy;

    public SingleThreadBinningStrategy(List<Windowable> thread, WindowStrategy.Factory<Windowable> factory) {
        List<Windowable> data = new ArrayList<Windowable>(thread);
        Collections.sort(data, new Comparator<Windowable>() {
            public int compare(Windowable argument, Windowable argument1) {
                return argument.getStart().compareTo(argument1.getStart());
            }
        });

        windowStrategy = factory.getStrategy();
        windowStrategy.setData(data);

    }


    public List<List<Windowable>> getDataAtWindow(int window) {
        return Collections.singletonList(windowStrategy.getWindow(window));
    }

    public int getNumBins() {
        return 1;
    }

    public int getNumWindows() {
        return windowStrategy.getNumberWindows();
    }


}
