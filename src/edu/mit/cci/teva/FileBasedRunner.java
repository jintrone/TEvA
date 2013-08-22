package edu.mit.cci.teva;

import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.cpm.cfinder.CFinderCommunityFinder;
import edu.mit.cci.teva.engine.*;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.util.NetWriterStrategy;
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
public abstract class FileBasedRunner implements TevaRunner{


    Logger log = Logger.getLogger(FileBasedRunner.class);
    TevaParameters params;
    Conversation conversation;
    private WindowStrategy.Factory<Windowable> windowStrategyFactory;
    private TevaFactory factory;


    public FileBasedRunner(Conversation c) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters();
        this.factory = new DefaultTevaFactory(params,conversation);
        //process();
    }

    public FileBasedRunner(Conversation c, InputStream parameters) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters(parameters);
        this.factory = new DefaultTevaFactory(params,conversation);
        //process();
    }

     public FileBasedRunner(Conversation c, InputStream parameters, TevaFactory factory) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = new TevaParameters(parameters);
        this.factory =factory;
        //process();
    }

    public FileBasedRunner(Conversation c, TevaParameters parameters, TevaFactory factory) throws IOException, CommunityFinderException, JAXBException {
        this.conversation = c;
        this.params = parameters;
        this.factory =factory;
        //process();
    }




    public void createNetworkFiles() throws IOException {
        NetWriterStrategy writer = getNetworkWriter();
        BinningStrategy<Windowable> binningStrategy =  factory.getTopicBinningStrategy(factory.getConversationData(), factory.getTopicWindowingFactory());
        CorpusToNetworkGenerator<Windowable> networkGenerator = factory.getNetworkGenerator(binningStrategy);
        networkGenerator.analyzeToFiles(writer);
        log.info("Done creating networks.");
    }

    public NetworkProvider getNetworkProvider() throws IOException {

        NetworkProvider provider;
        File f = new File(params.getWorkingDirectory());
        if (!f.exists()) {
            f.mkdirs();
        }

        final NetWriterStrategy writer = getNetworkWriter();

        if (!params.getSkipNetworkGeneration()) {
           createNetworkFiles();

        }
        WindowStrategy.Factory<Windowable> winFactory = factory.getTopicWindowingFactory();
        final int i = winFactory.getStrategy().getNumberWindows();
        provider = new NetworkProvider() {
            public int getNumberWindows() {
                return i;
            }

            public Network getNetworkAt(int i) {
                File f =  writer.getNetworkFilename(i);
                        //CFinderCommunityFinder.getInputNetworkFile(new File(params.getWorkingDirectory()),i,params.getFilenameIdentifier());
                try {
                    return NetworkUtils.readNetworkFile(f);
                } catch (IOException e) {
                    log.error("Error reading network file "+f.getAbsolutePath()+", cannot proceed");
                    throw new RuntimeException(e);
                }
            }
        };

        return provider;
    }

    public abstract NetWriterStrategy getNetworkWriter();

    public void process() throws IOException, CommunityFinderException, JAXBException {
        log.info("Begin process.");
        NetworkProvider provider = getNetworkProvider();
        CommunityModel model = new CommunityModel(params,factory.getTopicWindowingFactory().getStrategy().getWindowBoundaries(),conversation.getName());
        EvolutionEngine engine = new EvolutionEngine(model,params, provider, factory.getFinder(), factory.getStepper(model), factory.getMerger());
        engine.process();
        BinningMembershipEngine membershipEngine = new BinningMembershipEngine(model,conversation,factory);
        membershipEngine.process();
        TevaUtils.serialize(new File(params.getWorkingDirectory() + "/CommunityOutput." + conversation.getName() + "." + params.getFilenameIdentifier() + ".xml"), model, CommunityModel.class);

    }


}
