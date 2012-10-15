package edu.mit.cci.teva.analysis;

import edu.mit.cci.sna.Node;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.util.TevaUtils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/12/12
 * Time: 9:18 AM
 */
public class DumpTopicsToCsv {

    public  String[] headers = new String[]{"From", "To", "TopicID", "Short", "Long"};


    protected PrintWriter writer;
    protected CommunityModel model;

    public DumpTopicsToCsv(CommunityModel model, OutputStream stream) {
        writer = new PrintWriter(new OutputStreamWriter(stream));
        this.model = model;
    }


    public void write() {
        Date[][] wins = model.getWindows();
        printLine(headers);
        for (int i = 0; i < wins.length; i++) {
            for (Community c : model.getCommunities()) {
               Collection<String> s = c.getCommunityAtBin(i) == null ? Collections.<String>emptyList() : TevaUtils.getCommunityRepresentation(c.getCommunityAtBin(i), 5);
                String l = flatten(c.getCommunityAtBin(i) == null ? Collections.<Node>emptyList() : c.getCommunityAtBin(i).getNodes(),new Stringer<Node>() {

                    public String stringify(Node object) {
                        return object.getLabel();
                    }
                });
                printLine(wins[i][0], wins[i][1], c.getId(), s, l);

            }
        }
    }

    public void printLine(Object... r) {
        StringBuilder builder = new StringBuilder();
        String sep = "";
        for (Object o:r) {
            builder.append(sep).append(getString(o));
            sep = ",";
        }
        writer.println(builder.toString());

    }

    public String getString(Object o) {
        if (o == null) {
            return "";
        } else if (o instanceof String) {
            String rep = ((String) o).replace("\"", "\\\"");
            return "\""+rep+"\"";
        } else if (o instanceof Date) {
            return getString(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format((Date) o));
        } else if (o instanceof Number) {
            return o.toString();
        } else if (o instanceof Collection) {
            return getString(flatten((Collection)o,null));
        } else return getString(o.toString());
    }

    public String flatten(Collection c, Stringer s) {
        StringBuilder builder = new StringBuilder();
        String sep = "";
        for (Object o:c) {
            builder.append(sep).append(s!=null?s.stringify(o):o==null?"":o.toString());
            sep = ",";
        }
        return builder.toString();
    }

    public static interface Stringer<T> {
        public String stringify(T object);
    }


}
