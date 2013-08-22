package edu.mit.cci.teva.util;

import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.util.Filter;
import edu.mit.cci.util.U;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 5/22/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommunityRelationships {


    private Map<String, List<ResolvedConnection>> incoming = new HashMap<String, List<ResolvedConnection>>();


    public CommunityRelationships(CommunityModel model) {

        for (int i = 0; i < model.getWindows().length; i++) {
            Set<CommunityModel.Connection> conns = model.getConnection(i, CommunityModel.ConnectionType.CONSUMES);
            if (conns != null) {
                for (CommunityModel.Connection c : conns) {

                    ResolvedConnection in = new ResolvedConnection(c.source.getMaxBin(), i, c);
                    List<ResolvedConnection> list = incoming.get(c.target.getId());
                    if (list == null) {
                        incoming.put(c.target.getId(), list = new ArrayList<ResolvedConnection>());
                    }
                    list.add(in);

                }
            }
        }
    }


    public List<ResolvedConnection> getAllIncoming(String communityId) {
        return incoming.get(communityId);
    }


    public List<CommunityWindow> getAllIncoming(String communityId, final int window) {
        final List<CommunityWindow> result = new ArrayList<CommunityWindow>();

        U.filter(incoming.get(communityId), new Filter<ResolvedConnection>() {
            public boolean accept(ResolvedConnection obj) {
                if (window == obj.getTo()) {
                    result.add(new CommunityWindow(obj.getConnection().source.getId(), obj.getFrom()));
                    return true;
                } else return false;
            }
        });
        return result;
    }


    public List<CommunityWindow> getAllIncomingBefore(String communityId, final int window) {
        final List<CommunityWindow> result = new ArrayList<CommunityWindow>();

        U.filter(incoming.get(communityId), new Filter<ResolvedConnection>() {
            public boolean accept(ResolvedConnection obj) {
                if (window <= obj.getTo()) {
                    result.add(new CommunityWindow(obj.getConnection().source.getId(), obj.getFrom()));
                    return true;
                } else return false;
            }
        });
        return result;
    }
}
