package edu.mit.cci.text.windowing;

import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.util.F;
import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 4/17/13
 * Time: 3:31 PM
 */
public class ReplyBasedBinningStrategy implements BinningStrategy<Windowable> {

    int maxwin = -1;
    List<List<Bin<Windowable>>> binmodel = new ArrayList<List<Bin<Windowable>>>();
    private static Logger logger = Logger.getLogger(ReplyBasedBinningStrategy.class);


    public ReplyBasedBinningStrategy(List<List<WindowablePostAdapter>> info, WindowStrategy.Factory<Windowable> factory) {
        WindowStrategy<Windowable> strategy = factory.getStrategy();
        final List<WindowablePostAdapter> posts = new ArrayList<WindowablePostAdapter>();


        U.map(info).forEach(new F<List<WindowablePostAdapter>>() {
            public void apply(List<WindowablePostAdapter> windowablePostAdapters) {
                posts.addAll(windowablePostAdapters);
            }
        });

        Collections.sort(posts, new Comparator<Windowable>() {
            public int compare(Windowable post1, Windowable post2) {
                return post1.getStart().compareTo(post2.getStart());
            }
        });

        strategy.setData(posts);
        maxwin = Math.max(strategy.getNumberWindows(), maxwin);


        for (int i = 0; i < maxwin; i++)

        {
            List<Bin<Windowable>> chunks = new ArrayList<Bin<Windowable>>();
            chunks.addAll(analyzeChunk(strategy.getWindow(i)));
            binmodel.add(chunks);

        }

        //debugging
        logger.info("There are "+maxwin+" windows");
        for (int i=0;i<maxwin;i++) {
            List<Bin<Windowable>> bin = getDataAtWindow(i);
            logger.info(i+". "+bin.size());
            final Date[] boundaries = strategy.getWindowBoundaries()[i];
            U.map(bin).forEach(new F<Bin<Windowable>>() {
                public void apply(Bin<Windowable> windowables) {
                    U.map(windowables).forEach(new F<Windowable>() {
                        public void apply(Windowable windowable) {
                            if (windowable.getStart().before(boundaries[0]) ||
                                    windowable.getStart().after(boundaries[1])) {
                                logger.warn("----> Messages outside bin boundaries?");
                            }
                        }
                    });
                }
            });

        }


    }

    protected Collection<Bin<Windowable>> analyzeChunk(List<Windowable> chunk) {
        final Map<String, Bin<Windowable>> data = new HashMap<String, Bin<Windowable>>();
        final Map<String, WindowablePostAdapter> lookup = new HashMap<String, WindowablePostAdapter>();
        U.map(chunk).forEach(new F<Windowable>() {
            public void apply(Windowable p) {
                WindowablePostAdapter pa = (WindowablePostAdapter) p;
                lookup.put(p.getId(), pa);
                if (!data.containsKey(pa.getPost().getReplyToId())) {
                    Bin<Windowable> result = new BasicBin<Windowable>();
                    data.put(pa.getId(), result);
                    result.add(pa);

                } else {
                    Bin<Windowable> existing = data.get(pa.getPost().getReplyToId());

                    if (existing.get(existing.size() - 1).getId().equals(pa.getPost().getReplyToId())) {
                        existing.add(pa);
                        data.put(pa.getId(), existing);
                    } else {
                        Bin<Windowable> bin = new BasicBin<Windowable>();
                        data.put(pa.getId(), bin);
                        bin.add(lookup.get(pa.getPost().getReplyToId()));
                        bin.add(pa);
                        bin.setFirstItemIndex(1);
                    }


                }
            }
        });
        return new HashSet<Bin<Windowable>>(data.values());

    }


    public List<Bin<Windowable>> getDataAtWindow(int window) {
        return binmodel.get(window);
    }




    public int getNumWindows() {
        return maxwin;
    }

}
