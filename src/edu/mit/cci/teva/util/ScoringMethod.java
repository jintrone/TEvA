package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.engine.ConversationChunk;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 10:27 PM
 */
public interface ScoringMethod {

    public float score(Network one, Network two);
    public float fit(ConversationChunk chunk);

    public enum Method implements ScoringMethod {
        SIMILARITY() {
            public float score(Network one, Network two) {
                return NetworkUtils.similarity(one,two);
            }
            public float fit(ConversationChunk chunk) {return chunk.similarity;}

        },

        COVERAGE() {
            public float score(Network one, Network two) {
                return NetworkUtils.coverage(one,two);
            }
            public float fit(ConversationChunk chunk) {return chunk.coverage;}
        }


    }

}
