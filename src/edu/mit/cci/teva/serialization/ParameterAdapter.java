package edu.mit.cci.teva.serialization;

import edu.mit.cci.teva.engine.TevaParameters;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * User: jintrone
 * Date: 10/12/12
 * Time: 12:31 PM
 */


@XmlRootElement(name = "parameters")
public class ParameterAdapter extends XmlAdapter<ParameterAdapter,TevaParameters> {

    public ParameterAdapter(){}

    @XmlAttribute
    public float minimumLinkWeight = .5f;
    @XmlAttribute
    public int fixedCliqueSize = 4;
    @XmlAttribute
    public boolean expireConsumedCommunities = false;
    @XmlAttribute
    public String scoringMethod = "SIMILARITY";
    @XmlAttribute
    public String cfinderExecutable = "";
    @XmlAttribute
    public String filenameIdentifier = "";
    @XmlAttribute
    public String replacementDictionary = "";
    @XmlAttribute
    public String stopwordList = "";
    @XmlAttribute
    public long windowSize = -1;
    @XmlAttribute
    public long windowDelta = -1;
    @XmlAttribute
    public int wordijIndirection = -1;
    @XmlAttribute
    public int wordijTuple = -1;
    @XmlAttribute
    public long membershipWIndowSize=-1l;

    @XmlAttribute
    public long membershipWindowDelta = -1;

    public String workingDirectory = "";








    @Override
    public TevaParameters unmarshal(ParameterAdapter pa) throws Exception {
        TevaParameters params = new TevaParameters();
        params.setMinimumLinkWeight(pa.minimumLinkWeight);
        params.setFixedCliqueSize(pa.fixedCliqueSize);
        params.setExpireConsumedCommunities(pa.expireConsumedCommunities);
        params.setScoringMethod(pa.scoringMethod);
        params.setCFinderExecutable(pa.cfinderExecutable);
        params.setFilenameIdentifier(pa.filenameIdentifier);
        params.setReplacementDictionary(pa.replacementDictionary);
        params.setStopwordList(pa.stopwordList);
        params.setWindowSize(pa.windowSize);
        params.setWindowDelta(pa.windowDelta);
        params.setWordijIndirection(pa.wordijIndirection);
        params.setWordijTupleSize(pa.wordijTuple);
        params.setMembershipWindowSize(pa.membershipWIndowSize);
        params.setMembershipWindowDelta(pa.membershipWindowDelta);
        return params;
    }

    @Override
    public ParameterAdapter marshal(TevaParameters tp) throws Exception {
        ParameterAdapter result = new ParameterAdapter();
        result.minimumLinkWeight =tp.getMinimumLinkWeight();
        result.fixedCliqueSize = tp.getFixedCliqueSize();
        result.expireConsumedCommunities = tp.expireConsumedCommunities();
        result.scoringMethod = tp.getScoringMethod().toString();
        result.cfinderExecutable = tp.getCFinderExecutable();
        result.filenameIdentifier = tp.getFilenameIdentifier();
        result.replacementDictionary = tp.getReplacementDictionary();
        result.stopwordList = tp.getStopwordList();
        result.windowSize = tp.getWindowSize();
        result.windowDelta = tp.getWindowDelta();
        result.wordijIndirection = tp.getWordijIndirection();
        result.wordijTuple = tp.getWordijTupleSize();
        result.membershipWIndowSize = tp.getMembershipWindowSize();
        result.membershipWindowDelta = tp.getMembershipWindowDelta();
        return result;

    }
}
