package edu.mit.cci.text.windowing;

import java.util.Date;
import java.util.List;

/**
* User: jintrone
* Date: 9/24/12
* Time: 11:42 PM
*/
public interface Windowable {
    public Date getStart();
    public List<String> getTokens();
}
