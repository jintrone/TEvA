package edu.mit.cci.teva.analysis;

import edu.mit.cci.sna.Node;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.util.Stringer;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/12/12
 * Time: 2:02 PM
 */
public class DumpTopicsToCsvLong extends DumpTopicsToCsv {

    public List<String> headers = new ArrayList<String>(Arrays.asList("From", "To","Spawns","Consumes"));

    public DumpTopicsToCsvLong(CommunityModel model, OutputStream stream) {
        super(model, stream);

    }

    public void write() {
        for (Community c : model.getCommunities()) {
            headers.add("Topic" + c.getId());
        }
        Date[][] wins = model.getWindows();
        printLine(headers.toArray());
        List<Object> line = new ArrayList<Object>();

        for (int i = 0; i < wins.length; i++) {
            line.clear();
            line.add(wins[i][0]);
            line.add(wins[i][1]);
            line.add(model.getConnection(i, CommunityModel.ConnectionType.SPAWNS));
            line.add(model.getConnection(i, CommunityModel.ConnectionType.CONSUMES));


            for (Community c : model.getCommunities()) {
                //Collection<String> s = c.getCommunityAtBin(i) == null ? Collections.<String>emptyList() : TevaUtils.getCommunityRepresentation(c.getCommunityAtBin(i), 5);
                String l = flatten(c.getCommunityAtBin(i) == null ? Collections.<Node>emptyList() : c.getCommunityAtBin(i).getNodes(), new Stringer<Node>() {

                    public String stringify(Node object) {
                        return object.getLabel();
                    }
                });
                line.add(l);

            }
            printLine(line.toArray());
        }
        writer.flush();
        writer.close();

    }

}
