package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.engine.*;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.text.windowing.*;
import edu.uci.ics.jung.graph.Graph;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 8/22/13
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SocialCommunityFinder {



    private final TevaParameters params;
    private final TevaFactory factory;
    private WindowStrategy<Windowable> strategy;
    private Map<String,String> post2author = new HashMap<>();
    private Conversation conversation;


    Logger log = Logger.getLogger(SocialCommunityFinder.class);

    public SocialCommunityFinder(Conversation c, TevaParameters params, TevaFactory factory) {
         this.params = params;
        this.factory = factory;
        this.strategy = factory.getTopicWindowingFactory().getStrategy();
        this.conversation = c;
    }

    public void analyze() throws CommunityFinderException, IOException, JAXBException {
       List<Windowable> allposts = new ArrayList<Windowable>();
        for (List<Windowable> w:factory.getConversationData()) {
            allposts.addAll(w);
        }
        Collections.sort(allposts,new Comparator<Windowable>() {
            @Override
            public int compare(Windowable o1, Windowable o2) {
                return o1.getStart().compareTo(o2.getStart());
            }
        });
        strategy.setData((List<? extends Windowable>) allposts);

        log.info("Will process " + strategy.getNumberWindows() + " windows");
        final List<Network> results = new ArrayList<>();
        for (int win = 0; win < strategy.getNumberWindows(); win++) {
            log.debug("Process window " + win);

            results.add(analyzeWindow(win));
        }
        NetworkProvider provider = new NetworkProvider() {
            @Override
            public int getNumberWindows() {
                return results.size();
            }

            @Override
            public Network getNetworkAt(int i) {
               return results.get(i);
            }
        };


        log.info("Done processing windows");
        CommunityModel model = new CommunityModel(params,factory.getTopicWindowingFactory().getStrategy().getWindowBoundaries(),conversation.getName());
        EvolutionEngine engine = new EvolutionEngine(model,params, provider, factory.getFinder(), factory.getStepper(model), factory.getMerger());
        engine.process();
        TevaUtils.serialize(new File(params.getWorkingDirectory()+"/SocialOutput."+conversation.getName()+"."+params.getFilenameIdentifier()+".xml"), model, CommunityModel.class);
    }

    public Network analyzeWindow(int win) {
        UndirectedJungNetwork graph = new UndirectedJungNetwork();
        List<Windowable> posts = strategy.getWindow(win);


        for (Windowable w:posts) {
            Post p = ((WindowablePostAdapter)w).getPost();
            post2author.put(p.getPostid(),p.getUserid());
            if (p.getReplyToId() == null) continue;
            else if (!post2author.containsKey(p.getReplyToId())) {
                log.warn("Couldn't resolve reply-to id");
            } else {
                Node from = new NodeImpl(p.getUserid());
                Node to = new NodeImpl(post2author.get(p.getReplyToId()));
                Edge e = graph.findEdge(from,to);
                if (e!=null) {
                    e.setWeight(e.getWeight()+1.0f);
                } else {
                    graph.add(new EdgeImpl(from,to,1.0f,false));
                }
            }
        }
        return graph;
    }


}
