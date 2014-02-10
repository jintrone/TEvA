package edu.mit.cci.teva;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.cpm.cfinder.CFinderCommunityFinder;
import edu.mit.cci.teva.engine.*;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.util.TevaUtils;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.CorpusToNetworkGenerator;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/3/12
 * Time: 9:39 PM
 */
public class MemoryBasedRunner implements TevaRunner {


    Logger log = Logger.getLogger(MemoryBasedRunner.class);
    TevaParameters params;
    Conversation conversation;
    private WindowStrategy.Factory<Windowable> windowStrategyFactory;
    private TevaFactory factory;


    public MemoryBasedRunner(Conversation c) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters();
        this.factory = new DefaultTevaFactory(params,conversation);
        //process();
    }

    public MemoryBasedRunner(Conversation c, InputStream parameters) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters(parameters);
        this.factory = new DefaultTevaFactory(params,conversation);
        //process();
    }

     public MemoryBasedRunner(Conversation c, InputStream parameters, TevaFactory factory) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters(parameters);
        this.factory =factory;
        //process();
    }

    public MemoryBasedRunner(Conversation c, TevaParameters parameters, TevaFactory factory) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = parameters;
        this.factory =factory;

        //process();
    }





    public NetworkProvider getNetworkProvider() throws IOException {

        NetworkProvider provider;
        File f = new File(params.getWorkingDirectory());
        if (!f.exists()) {
            f.mkdirs();
        }

        if (params.getSkipNetworkGeneration()) {
            WindowStrategy.Factory<Windowable> winFactory = factory.getTopicWindowingFactory();
            final int i = winFactory.getStrategy().getNumberWindows();
            provider = new NetworkProvider() {
                public int getNumberWindows() {
                    return i;
                }

                public Network getNetworkAt(int i) {
                    File f = CFinderCommunityFinder.getNetworkFile(new File(params.getWorkingDirectory()),i,params.getFilenameIdentifier());
                    try {
                        return NetworkUtils.readNetworkFile(f);
                    } catch (IOException e) {
                        log.error("Error reading network file "+f.getAbsolutePath()+", cannot proceed");
                        throw new RuntimeException(e);
                    }
                }
            };
        } else {
            BinningStrategy<Windowable> binningStrategy =  factory.getTopicBinningStrategy(factory.getConversationData(), factory.getTopicWindowingFactory());

            CorpusToNetworkGenerator<Windowable> networkGenerator = new CorpusToNetworkGenerator<Windowable>(binningStrategy, factory.getNetworkCalculator());
            final List<Network> result = networkGenerator.analyzeToMemory();
            log.info("Done creating networks.");
            provider = new NetworkProvider() {
                public int getNumberWindows() {
                    return result.size();
                }

                public Network getNetworkAt(int i) {
                    return result.get(i);
                }
            };
        }

        return provider;
    }

    public CommunityModel process() throws IOException, CommunityFinderException {
        log.info("Begin process.");
        Community.reset();
        NetworkProvider provider = getNetworkProvider();
        CommunityModel model = new CommunityModel(params,factory.getTopicWindowingFactory().getStrategy().getWindowBoundaries(),conversation.getName());
        EvolutionEngine engine = new EvolutionEngine(model,params, provider, factory.getFinder(), factory.getStepper(model), factory.getMerger());
        engine.process();
        TopicMembershipEngine membershipEngine = factory.getMembershipEngine(model,conversation);
        membershipEngine.process();
        return model;
    }

    public void processAndSave() throws IOException, CommunityFinderException, JAXBException {

        TevaUtils.serialize(new File(params.getWorkingDirectory()+"/CommunityOutput."+conversation.getName()+"."+params.getFilenameIdentifier()+".xml"), process(), CommunityModel.class);

    }


}
