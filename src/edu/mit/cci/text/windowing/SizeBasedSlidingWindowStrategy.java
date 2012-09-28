package edu.mit.cci.text.windowing;


import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Determines a windowing based on the number of tokens in an underlying stream of data.
 *
 * Each returned window will be at least a given width. Subsequent windows always remove at least one data
 * data item from the trailing edge of the window, and then adds at least one new data item until the window is >=
 * the desired width
 *
 *
 * <p/>
 * <p/>
 * User: jintrone
 * Date: 5/7/12
 * Time: 3:47 AM
 */
public class SizeBasedSlidingWindowStrategy<T extends Windowable> implements WindowStrategy {

    private int numTokensWidth = -1;

    private int numTokensDelta = -1;


    List<Date[]> windows = new ArrayList<Date[]>();

    private List<T> current;

    private static Logger log = Logger.getLogger(SizeBasedSlidingWindowStrategy.class);

    /**
     *
     * @param numTokensWidth The minimum number of tokens in a window
     * @param numTokensDelta The minimum offset between subsequent windows in the data stream.
     */
    public SizeBasedSlidingWindowStrategy(int numTokensWidth, int numTokensDelta) {
        this.numTokensWidth = numTokensWidth;
        this.numTokensDelta = numTokensDelta;

    }


//

    private static class DateWidth {
        Date date;
        int width;

        public DateWidth(Date date, int width) {
            this.date = date;
            this.width = width;
        }

    }

    public void analyze() {
        List<DateWidth> buffer = new ArrayList<DateWidth>();
        int wcount = 0;
        Iterator<T> it = current.iterator();
        while (it.hasNext()) {
            T next = it.next();
            DateWidth dw = new DateWidth(next.getStart(), next.getSize());
            buffer.add(dw);

            wcount += dw.width;

            if (wcount >= numTokensWidth || !it.hasNext()) {
                Date[] d = new Date[]{buffer.get(0).date, buffer.get(buffer.size() - 1).date};
                windows.add(d);
                log.debug("Add window from " + d[0] + " to  " + d[1] + " with " + buffer.size() + " posts and " + wcount + " tokens");
                int temp = 0;
                while (temp < numTokensDelta ) {
                    int size = buffer.remove(0).width;
                    temp+= size;


                }
                wcount-=temp;


            }




        }

    }


    public int getNumberWindows() {
        return windows.size();
    }

    public List<T> getWindow(int i) {
        int start = U.binarySearch(current, windows.get(i)[0], new Comparator<Date>() {

                    public int compare(Date post, Date post1) {
                        return post.compareTo(post1);
                    }
                }, new U.Adapter<T, Date>() {

            public Date adapt(T obj) {
                return obj.getStart();
            }
        }
        );
        if (start < 0) {
            start = -1 * (++start);
        }
        if (start == current.size()) {
            return Collections.emptyList();
        } else {
            int end = start;
            while (end < current.size() && current.get(end).getStart().compareTo(windows.get(i)[1]) <= 0) {
                end++;
            }
            return current.subList(start, end);
        }


    }


    public void setData(List data) {
        this.current = data;
        analyze();
    }

//    public static long span(Date[] one) {
//        return Math.abs(one[1].getTime()-one[0].getTime());
//    }


//public static SizeBasedSlidingWindowingStrategy read(BufferedReader in) throws IOException {
//        String s;
//        SizeBasedSlidingWindowingStrategy result = null;
//        while((s = in.readLine())!=null) {
//            if (s.trim().isEmpty()) continue;
//            if (s.startsWith("#")) {
//                String[] p = s.substring(1).split(";");
//                 result = new SizeBasedSlidingWindowingStrategy(Integer.parseInt(p[0].trim()),Integer.parseInt(p[1].trim()));
//            } else {
//                String[] d = s.split(";");
//                for (int i=0;i<d.length;i+=2) {
//                    result.windows.add(new Date[]{new Date(Long.parseLong(d[i])),new Date(Long.parseLong(d[i+1]))});
//                }
//            }
//
//        }
//        return result;
//    }


    //    public void analyze(Iterator<T> p) {
//        List<DateWidth> buffer = new ArrayList<DateWidth>();
//        int wcount = 0;
//
//        while (p.hasNext()) {
//           T next = p.next();
//           DateWidth dw = new DateWidth(next.getStart(),next.getSize());
//           buffer.add(dw);
//
//           if (wcount > numTokensWidth || !p.hasNext()) {
//               Date[] d = new Date[] {buffer.get(0).date,buffer.get(buffer.size()-1).date};
//               windows.add(d);
//               log.debug("Add window from "+d[0]+" to  "+d[1]+" with "+buffer.size()+" posts and "+wcount+" tokens");
//               if (windows.size()>1 && d[0].equals(windows.get(windows.size()-2)[1])) {
//                   log.info("Detected no overlap!");
//               }
//               int temp = 0;
//               while (temp < numTokensDelta) {
//                  temp+=buffer.remove(0).width;
//
//               }
//               wcount-=temp;
//           }
//            wcount+=dw.width;
//
//        }
//
//    }

//    public static void main(String[] args) throws IOException {
//        DataContext context = DataContext.createDataContext("KickballData");
//        AdapterRepository repo = AdapterRepository.instance();
//        List<Post> posts = new ArrayList<Post>();
//        for (edu.mit.cci.snatools.datasets.kickballforums.Post kbp:KickballUtils.queryPosts(context,null,null)) {
//           posts.add(repo.get(kbp));
//        }
//        log.debug("Will process "+posts.size()+" posts");
//        FixedContentWindowingStrategy windows = new FixedContentWindowingStrategy(500,400,KickballUtils.getStopwordMungerFactory().getWordMunger());
//        windows.analyze(posts.iterator());
//        log.info("Got "+windows.getNumberWindows());
//        Date[] min=null, max=null;
//        int minp=0,maxp=0;
//
//        windows.setData(posts);
//
//        for (int i = 0;i<windows.getNumberWindows();i++) {
//
//            if (min == null) {
//                min = windows.windows.get(i);
//                max = windows.windows.get(i);
//                minp=maxp=windows.getWindow(i).size();
//            } else {
//                if (span(min) > span(windows.windows.get(i))) {
//                    min = windows.windows.get(i);
//                }   minp = windows.getWindow(i).size();
//                if (span(max) < span(windows.windows.get(i))) {
//                    max = windows.windows.get(i);
//                    maxp = windows.getWindow(i).size();
//                }
//            }
//        }
//
//        log.info("Min window is "+min[0]+" to "+min[1]+" ("+minp+" posts)");
//        log.info("Max window is "+max[0]+" to "+max[1]+" ("+maxp+" posts)");
//        PrintStream out = new PrintStream(new FileOutputStream("FixedWindows_500.400.txt"));
//        windows.write(out);
//        out.flush();
//        out.close();

//        BufferedReader in = new BufferedReader(new FileReader("FixedWindows_1000.txt"));
//
//        FixedContentWindowingStrategy windows2 = FixedContentWindowingStrategy.read(in,KickballUtils.getStopwordMungerFactory().getWordMunger());
//        log.info("Deserialized windower has "+windows2.getNumberWindows());
//        windows2.setData(posts);
//        Date[] min = null;
//        Date[] max = null;
//        int minp = 0,maxp=0;
//        for (int i = 0;i<windows2.getNumberWindows();i++) {
//
//            if (min == null) {
//                min = windows2.windows.get(i);
//                max = windows2.windows.get(i);
//                minp=maxp=windows2.getWindow(i).size();
//            } else {
//                if (span(min) > span(windows2.windows.get(i))) {
//                    min = windows2.windows.get(i);
//                }   minp = windows2.getWindow(i).size();
//                if (span(max) < span(windows2.windows.get(i))) {
//                    max = windows2.windows.get(i);
//                    maxp = windows2.getWindow(i).size();
//                }
//            }
//        }
//        log.info("Min window (DS) is "+min[0]+" to "+min[1]+" ("+minp+" posts)");
//        log.info("Max window (DS) is "+max[0]+" to "+max[1]+" ("+maxp+" posts)");


}
