package edu.mit.cci.teva.analysis;

import com.Ostermiller.util.ExcelCSVPrinter;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.jung.DirectedJungNetwork;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.engine.ConversationChunk;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.CommunityWindow;
import edu.mit.cci.teva.util.Stringer;
import edu.mit.cci.teva.util.TevaUtils;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.*;

/**
 * User: jintrone
 * Date: 5/14/13
 * Time: 3:44 PM
 */
public class DumpCentralityRankedConversationToCsv {

    public String[] headers = new String[]{"Topic", "Date", "ThreadId", "MessageId","ReplyTo", "Consumes", "MaxCentrality", "Author", "Text"};



    protected ExcelCSVPrinter printer;
    protected CommunityModel model;
    protected Conversation conversation;
    protected Stringer<String> textStringer;

    private static Logger log = Logger.getLogger(DumpCentralityRankedConversationToCsv.class);

    public DumpCentralityRankedConversationToCsv(CommunityModel model, Conversation conversation, OutputStream stream) throws UnsupportedEncodingException {
        printer = new ExcelCSVPrinter(stream);
        this.model = model;
        this.conversation = conversation;
    }

    public void setTextStringer(Stringer<String> stringer) {
        this.textStringer = stringer;
    }


    public void write() throws IOException {

        DirectedJungNetwork network = TevaUtils.createCommunityGraph(model, false, true, false);
        TevaUtils.addDrainageScoresForCommunityGraph(network);

        Map<String, Community> cmap = new HashMap<String, Community>();
        Map<String,Post> postmap = new HashMap<String,Post>();
        Map<String,List<CommunityWindow>> windowConsumers = new HashMap<String,List<CommunityWindow>>();

        for (DiscussionThread thread:conversation.getAllThreads()) {
            for (Post p:thread.getPosts()) {
                postmap.put(p.getPostid(),p);
            }
        }

        final Map<String, Integer> centrality = new HashMap<String, Integer>();
        for (Community c : model.getCommunities()) {
            cmap.put(c.getId(), c);
            windowConsumers.put(c.getId(),new ArrayList<CommunityWindow>());

        }
        for (Node n : network.getNodes()) {
            String cid = (String) n.getProperty("CommunityId");
            if (!centrality.containsKey(cid) || centrality.get(cid) < (Integer) n.getProperty("Centrality")) {
                centrality.put(cid, (Integer) n.getProperty("Centrality"));
            }

            for (Edge e:network.getInEdges(n)) {
                Node src = e.getEndpoints()[0];
                if (!cid.equals(src.getProperty("CommunityId"))) {
                    windowConsumers.get(cid).add(new CommunityWindow((String)src.getProperty("CommunityId"),(Integer)n.getProperty("Window")));
                }
            }

        }
        List<String> communities = new ArrayList();

        communities.addAll(centrality.keySet());

        Collections.sort(communities,new Comparator<String>() {
            public int compare(String s, String s2) {
                return -1*(centrality.get(s).compareTo(centrality.get(s2)));
            }
        });
        printLine(headers);

        for (String community:communities) {
            Community c = cmap.get(community);
            Set<Windowable> posts = new TreeSet<Windowable>(new Comparator<Windowable>() {
                public int compare(Windowable windowable, Windowable windowable2) {
                    return windowable.getStart().compareTo(windowable2.getStart());
                }
            });
            if (c!=null && c.getAssignments()!=null) {
                log.info("Found "+c.getAssignments().size()+" chunks for "+c.getId());
                for (ConversationChunk chunk :c.getAssignments()) {
                    posts.addAll(chunk.messages);
                    log.info("Adding "+chunk.messages.size()+" messages ");

                }
            }
            List<CommunityWindow> consumes = windowConsumers.get(c.getId());

            for (Windowable w:posts) {
                String consumesString = "--";
                if (!consumes.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    for (CommunityWindow cw:consumes) {
                        Date[] window = model.getWindows()[cw.window];
                        if (inWindow(w.getStart(),window)) {
                            builder.append("[").append(cw.community).append("]");
                        }

                    }
                    consumesString = builder.toString();

                }
                printLine(c.getId(),w.getStart(),postmap.get(w.getId()).getThreadid(),w.getId(),postmap.get(w.getId()).getReplyToId(),consumesString,centrality.get(community),postmap.get(w.getId()).getUserid(),w.getRawData());
            }
        }
        printer.flush();
        printer.close();




    }

    private boolean inWindow(Date d, Date[] bounds) {
        return (d.equals(bounds[0]) || d.after(bounds[0])) && d.before(bounds[1]);
    }

    public void printLine(Object... r) {
        String[] result = new String[r.length];
        for (int i = 0;i<r.length;i++) {
            result[i] = getString(r[i]);
        }

        printer.println(result);

    }

    public String getString(Object o) {
        if (o == null) {
            return "";
        } else if (o instanceof String) {
           // String rep = ((String) o).replace("\"", "\\\"");
           // return "\"" + rep + "\"";
            return (textStringer!=null)?textStringer.stringify((String)o):((String)o);

        } else if (o instanceof Date) {
            return getString(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format((Date) o));
        } else if (o instanceof Number) {
            return o.toString();
        } else if (o instanceof Collection) {
            return getString(flatten((Collection) o, null));
        } else return getString(o.toString());
    }

    public String flatten(Collection c, Stringer s) {
        StringBuilder builder = new StringBuilder();
        String sep = "";
        for (Object o : c) {
            builder.append(sep).append(s != null ? s.stringify(o) : o == null ? "" : o.toString());
            sep = ",";
        }
        return builder.toString();
    }




}
