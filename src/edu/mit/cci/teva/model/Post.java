package edu.mit.cci.teva.model;

import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 8/10/11
 * Time: 5:20 PM
 */
public interface Post  {
    String getThreadid();

    String getPostid();

    String getReplyToId();

    String getUserid();

    Date getTime();

    List<String> getSentiment();

    String getContent();


}
