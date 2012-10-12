package edu.mit.cci.teva.serialization;

import edu.mit.cci.teva.engine.CommunityFrame;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 10/8/12
 * Time: 10:19 PM
 */

@XmlRootElement(name = "frames")
public class CommunityFrameMapJaxbAdapter extends XmlAdapter<CommunityFrameMapJaxbAdapter,Map<Integer,CommunityFrame>> {

    @XmlElement(name="frame")
    List<CommunityFrame> frames;

    public CommunityFrameMapJaxbAdapter() {

    }

     public CommunityFrameMapJaxbAdapter(List<CommunityFrame> frames) {
         this.frames = new ArrayList<CommunityFrame>(frames);

    }

    @Override
    public Map<Integer, CommunityFrame> unmarshal(CommunityFrameMapJaxbAdapter communityFrameMapJaxbAdapter) throws Exception {
        Map<Integer,CommunityFrame> result = new HashMap<Integer, CommunityFrame>();
        for (CommunityFrame f:communityFrameMapJaxbAdapter.frames) {
          result.put(f.getWindow(),f);
        }
        return result;
    }

    @Override
    public CommunityFrameMapJaxbAdapter marshal(Map<Integer, CommunityFrame> integerCommunityFrameMap) throws Exception {
        List<CommunityFrame> f = new ArrayList<CommunityFrame>(integerCommunityFrameMap.values());
        Collections.sort(f,new Comparator<CommunityFrame>() {
            public int compare(CommunityFrame communityFrame, CommunityFrame communityFrame1) {
                return communityFrame.getWindow()-(communityFrame1.getWindow());
            }
        });
        CommunityFrameMapJaxbAdapter adapter =  new CommunityFrameMapJaxbAdapter(f);
        return adapter;
    }
}
