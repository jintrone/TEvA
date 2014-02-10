package edu.mit.cci.text.windowing;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Basic implementation of a binning strategy.  Presumes N threads, evolving in parallel.  Ignores reply structure if it exists.
 *
 * User: jintrone
 * Date: 5/23/11
 * Time: 5:33 PM
 */
public class BasicBinningStrategy<T extends Windowable> implements BinningStrategy<Windowable> {


    public List<WindowStrategy<Windowable>> binmodel = new ArrayList<WindowStrategy<Windowable>>();
    private static Logger log = Logger.getLogger(BasicBinningStrategy.class);
    private int maxwin = 0;

    public BasicBinningStrategy(List<List<Windowable>> info, WindowStrategy.Factory<Windowable> factory) {

        for (List<Windowable> bin : info) {
            Collections.sort(bin, new Comparator<Windowable>() {
                public int compare(Windowable argument, Windowable argument1) {
                    return argument.getStart().compareTo(argument1.getStart());
                }
            });
        }

        Collections.sort(info, new Comparator<List<Windowable>>() {

            public int compare(List<Windowable> arguments, List<Windowable> arguments1) {
                Windowable arg0 = arguments.get(0);
                Windowable arg1 = arguments1.get(0);
                return arg0.getStart().compareTo(arg1.getStart());
            }
        });
        int i = 0;
        for (List<Windowable> args : info) {
            WindowStrategy<Windowable> strategy = factory.getStrategy();
            strategy.setData(args);
            maxwin = Math.max(maxwin,strategy.getNumberWindows());
            binmodel.add(strategy);
        }
    }


    public List<Bin<Windowable>> getDataAtWindow(int window) {
        List<Bin<Windowable>> result = new ArrayList<Bin<Windowable>>();
        for (WindowStrategy<Windowable> strategy:binmodel) {
            result.add(new BasicBin<Windowable>(strategy.getWindow(window)));
        }
        return result;
    }





    public int getNumBins() {
        return binmodel.size();
    }

    public int getNumWindows() {
        return maxwin;
    }



}
