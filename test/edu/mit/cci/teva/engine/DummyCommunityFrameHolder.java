package edu.mit.cci.teva.engine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: jintrone
 * Date: 10/8/12
 * Time: 8:43 PM
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DummyCommunityFrameHolder {

    public DummyCommunityFrameHolder() {}

    private CommunityFrame frame;

    @XmlElement(name="frame")
    public CommunityFrame getFrame() {
        return frame;
    }

    public void setFrame(CommunityFrame frame) {

        this.frame = frame;
    }


    public DummyCommunityFrameHolder(CommunityFrame frame) {
        this.frame = frame;
    }

}
