package edu.mit.cci.teva.engine;

import edu.mit.cci.sna.Network;
import edu.mit.cci.text.windowing.Bin;
import edu.mit.cci.text.windowing.Windowable;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 8/30/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CommunityMembershipStrategy {

    public Map<Community,List<ConversationChunk>> assignToCommunity(CommunityModel communities,int window, Network net, Bin<Windowable> bin);

}
