package edu.mit.cci.text.windowing;

import java.util.Date;

/**
* User: jintrone
* Date: 9/24/12
* Time: 11:42 PM
*/
public interface Windowable {
    public Date getStart();
    public Date getEnd();
    public int getSize();
}
