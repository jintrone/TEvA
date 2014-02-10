package edu.mit.cci.text.windowing;

import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.TevaUtils;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * User: jintrone
 * Date: 9/29/12
 * Time: 10:32 AM
 */
public class WindowingUtils {


    private static Logger log = Logger.getLogger(WindowingUtils.class);

    public static <T extends Windowable> Date[][] analyzeSingleThreadBySize(List<T> current, int numTokensWidth, int numTokensDelta) {
        List<DateWidth> buffer = new ArrayList<DateWidth>();
        List<Date[]> windows = new ArrayList<Date[]>();
        int wcount = 0;
        Iterator<T> it = current.iterator();
        while (it.hasNext()) {
            T next = it.next();
            DateWidth dw = new DateWidth(next.getStart(), next.getTokens().size());
            buffer.add(dw);

            wcount += dw.width;

            if (wcount >= numTokensWidth || !it.hasNext()) {
                Date[] d = new Date[]{buffer.get(0).date, buffer.get(buffer.size() - 1).date};
                windows.add(d);
                int temp = 0;
                while (temp < numTokensDelta) {
                    int size = buffer.remove(0).width;
                    temp += size;


                }
                wcount -= temp;


            }


        }
        return windows.toArray(new Date[windows.size()][]);

    }


    /**
     * Seeks to analyze multiple threads to obtain a set of windows in which the maximum thread length is
     *
     * @param current
     * @param numTokensWidth
     * @param numTokensDelta
     * @param <T>
     * @return
     */
    public static <T extends Windowable> Date[][] analyzeMultipleThreadsBySize(Conversation c, Tokenizer<String> t, int numTokensWidth, int numTokensDelta) {

        List<Date[]> windows = new ArrayList<>();
        List<Post> posts = TevaUtils.getAllSortedPosts(c);
        int target = numTokensWidth - numTokensDelta;


        Map<String, Integer> totals = new HashMap<>();
        List<Integer> sizes = new ArrayList<>();

        int firstInWin = 0;
        windows.add(new Date[]{posts.get(0).getTime(), null});

        for (int i = 0; i < posts.size(); i++) {
            if (i%1000 == 0) {
                log.info("Processing post "+i);
            }
            Post p = posts.get(i);
            List<String> tok = t.tokenize(posts.get(i).getContent());
            sizes.add(tok.size());
            int val = (totals.containsKey(p.getThreadid())?totals.get(p.getThreadid()):0) + tok.size();
            totals.put(p.getThreadid(),val);

            if (val > numTokensWidth) {
                U.last(windows)[1] = p.getTime();
                if (i == posts.size() - 1) break;

                for (int delta = 0; delta < numTokensDelta; firstInWin++) {
                    int size = sizes.remove(0);

                    String tid = posts.get(firstInWin).getThreadid();
                    totals.put(tid, totals.get(tid) - size);
                    if (tid.equals(p.getThreadid())) {
                        delta+=size;
                    }

                }

                windows.add(new Date[]{posts.get(firstInWin).getTime(), null});
            }
        }
        if (U.last(windows)[1] == null) {
            U.last(windows)[1] = U.last(posts).getTime();
        }
        return windows.toArray(new Date[windows.size()][]);
    }

    static class DateWidth {
        Date date;
        int width;

        public DateWidth(Date date, int width) {
            this.date = date;
            this.width = width;
        }

    }
}
