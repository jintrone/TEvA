package edu.mit.cci.teva.engine;


import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.util.SimilarityBasedAssignment;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.Bin;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.TextToNetworkGenerator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 6/20/11
 * Time: 9:37 PM
 */
public class BinningMembershipEngine implements TopicMembershipEngine {


    Conversation info;


    CommunityModel communities;
    private TevaFactory factory;

    private CommunityMembershipStrategy strategy = new SimilarityBasedAssignment();

    private Map<Integer, Integer> activeThreads = new HashMap<Integer, Integer>();

    private static Logger log = Logger.getLogger(BinningMembershipEngine.class);

    public BinningMembershipEngine(CommunityModel communities, Conversation info, TevaFactory factory) {
        this.info = info;


        this.communities = communities;
        this.factory = factory;

    }

    public void process() throws IOException {
        log.debug("Running topic membership assignment");
        List<List<Windowable>> data = factory.getConversationData();
        BinningStrategy<Windowable> binningStrategy = factory.getTopicBinningStrategy(factory.getConversationData(), factory.getTopicWindowingFactory());
        Tokenizer<String> tokenizer = factory.getTokenizer();
        TextToNetworkGenerator generator = factory.getNetworkCalculator();

        List<String> priorTokens;
        for (int i = 0; i < binningStrategy.getNumWindows(); i++) {
            List<Bin<Windowable>> dataAtWin = binningStrategy.getDataAtWindow(i);

            for (Bin<Windowable> dataInBin : dataAtWin) {
                Network net = generator.calculateWeights(getTokens(0, dataInBin.getFirstItemIndex(), dataInBin), getTokens(dataInBin.getFirstItemIndex(), dataInBin.size(), dataInBin));
                assignToCommunity(i, net, dataInBin);
            }

        }
        log.debug("Done topic membership assignment");
    }

    public void assignToCommunity(int window, Network net, Bin<Windowable> bin) {
        Map<Community,List<ConversationChunk>> result = strategy.assignToCommunity(communities,window,net,bin);
        for (Map.Entry<Community,List<ConversationChunk>> ent:result.entrySet()) {
            for (ConversationChunk chunk:ent.getValue()) {
               ent.getKey().addAssignment(chunk);
            }
        }



    }


    public List<String> getTokens(int start, int end, Bin<Windowable> bin) {
        List<String> result = new ArrayList<String>();
        for (int i = start; i < end; i++) {
            result.addAll(bin.get(i).getTokens());
        }
        return result;


    }


    public static void main(String[] args) {

    }


}
