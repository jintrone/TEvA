package edu.mit.cci.teva.util;

import edu.mit.cci.teva.engine.CommunityModel;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 5/22/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResolvedConnection {

    private final CommunityModel.Connection connection;
    private final int from;

    public int getTo() {
        return to;
    }

    public CommunityModel.Connection getConnection() {
        return connection;
    }

    public int getFrom() {
        return from;
    }

    private final int to;

    public ResolvedConnection(int from, int to, CommunityModel.Connection connection) {
        this.connection = connection;
        this.from = from;
        this.to = to;

    }


}
