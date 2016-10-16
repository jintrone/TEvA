package edu.mit.cci.teva.analysis;

import edu.mit.cci.teva.model.Conversation;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by josh on 10/16/16.
 */
public class DefaultAnalysisConsole extends AnalysisConsole {
    public DefaultAnalysisConsole() throws IOException {
        super();
    }

    public DefaultAnalysisConsole(String filename) throws IOException {
        super(filename);
    }

    @Override
    public Conversation getConversation() throws IOException, ParseException {
        throw new RuntimeException("Get conversation is not implemented");
    }

    public static void main(String[] args) throws IOException {
        new DefaultAnalysisConsole().start();
    }
}
