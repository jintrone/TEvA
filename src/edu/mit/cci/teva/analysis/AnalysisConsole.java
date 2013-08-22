package edu.mit.cci.teva.analysis;

import com.Ostermiller.util.ExcelCSVPrinter;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.jung.DirectedJungNetwork;
import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.CommunityWindow;
import edu.mit.cci.teva.util.MessageToModelMap;
import edu.mit.cci.teva.util.TevaUtils;
import edu.mit.cci.util.ConsoleBase;
import edu.mit.cci.util.ConsoleDocumentation;
import edu.mit.cci.util.U;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/12/12
 * Time: 9:33 AM
 */
public abstract class AnalysisConsole extends ConsoleBase {

    private Properties props;
    File pfile = new File("./.teva-analysis.properties");


    public AnalysisConsole() throws IOException {
        props = new Properties();
        if (!pfile.exists()) {
            pfile.createNewFile();
            updateProperties(new File("."));

        } else {
            props.load(new FileReader(pfile));
        }

        start();


    }


    private void updateProperties(File f) {
        try {
            PrintWriter writer = new PrintWriter(pfile);
            props.setProperty("last_file", f.getAbsolutePath());
            props.store(writer, "Teva Analysis Properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public File getAnyFile(String message) {
        File f = U.getAnyFile(message, props.getProperty("last_file"), javax.swing.JFileChooser.FILES_ONLY);
        if (f != null) updateProperties(f);
        return f;
    }




    @ConsoleDocumentation(value= "Create community graph")
    public void runCreateCommunityGraph() throws IOException, JAXBException {
        CommunityModel model = TevaUtils.getCommunityModelFromFile(U.getAnyFile("Select community file", ".", javax.swing.JFileChooser.FILES_ONLY));

        String filename = askUser("Enter a filename:");
        boolean informs = false;
        boolean spawns = false;
        System.out.print("Include informs links (y/n)? ");
        String response = s.nextLine();
        if ("y".equals(response.trim())) {
            informs = true;
        }
        System.out.print("Include spawns links (y/n)? ");
        response = s.nextLine();
        if ("y".equals(response.trim())) {
            spawns = true;
        }
        DirectedJungNetwork network = TevaUtils.createCommunityGraph(model, spawns, true, informs);
        TevaUtils.addDrainageScoresForCommunityGraph(network);
        JungUtils.writeGraphML(network, U.mapify("Size", 0, "Window", 0, "CommunityId", "", "Centrality", 0), filename + ".graphml");
    }

    @ConsoleDocumentation(value = "Convert to CSV")
    public void runCreateCSV() throws IOException, JAXBException, ParseException {

        File f = U.getAnyFile("Choose a community model", ".", javax.swing.JFileChooser.FILES_ONLY);
        Conversation conversation = getConversation();

        CommunityModel c = TevaUtils.getCommunityModelFromFile(f);
        DirectedJungNetwork network = TevaUtils.createCommunityGraph(c, false, true, true);
        MessageToModelMap mmap = new MessageToModelMap(c);

        for (Community comm : c.getCommunities()) {
            comm.getAssignments();

        }
        ExcelCSVPrinter printer = new ExcelCSVPrinter(new FileOutputStream(f.getName() + ".messages.csv"));
        String[] headers = new String[]{"timestamp", "authorid", "postid", "replytoid", "topic"};
        printer.println(headers);

        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        Set<String> topics = new HashSet<String>();
        for (DiscussionThread t : conversation.getAllThreads()) {
            for (Post p : t.getPosts()) {
                CommunityWindow cw = mmap.getBestCommunityWindow(p.getPostid());
                if (cw != null) topics.add(cw.community);
                String[] line = new String[]{format.format(p.getTime()),
                        p.getUserid(), p.getPostid(), p.getReplyToId(), cw == null ? null : cw.community};
                printer.println(line);
            }
        }

        printer.flush();
        printer.close();

        printer = new ExcelCSVPrinter(new FileOutputStream(f.getName() + ".links.csv"));
        printer.println(new String[]{"from_topic", "to_topic", "from_time", "to_time"});
        for (Node from : network.getNodes()) {
            Collection<Edge> edges = network.getInEdges(from);
            for (Edge e : edges) {
                Node to = network.getOpposite(from, e);
                String type = (String) e.getProperty("Type");
                if (type != null && !type.isEmpty()) {

                    if (topics.contains((String) from.getProperty("CommunityId")) && topics.contains((String) to.getProperty("CommunityId"))) {
                        String[] line = new String[]{
                                (String) from.getProperty("CommunityId"),
                                format.format(c.getWindows()[(Integer) from.getProperty("Window")][1]),
                                (String) to.getProperty("CommunityId"),
                                format.format(c.getWindows()[(Integer) to.getProperty("Window")][0])
                        };
                        printer.println(line);
                    }
                }


            }


        }

        printer.flush();
        printer.close();
    }

    public abstract Conversation getConversation() throws IOException, ParseException;


    @ConsoleDocumentation(value = "Write stacked topic CSV.")
    public void runNarrowTopicCSV() throws IOException, JAXBException {

        File f = getAnyFile("Choose a community analysis file");
        System.out.print("Enter an output filename:");
        String name = s.nextLine();
        if (!name.endsWith(".csv")) {
            name += ".csv";
        }
        new DumpTopicsToCsv(TevaUtils.getCommunityModelFromFile(f), new FileOutputStream(name)).write();

    }

    @ConsoleDocumentation(value = "Write long topic CSV.")
    public void runWideTopicCSV() throws IOException, JAXBException {

        File f = getAnyFile("Choose a community analysis file");
        System.out.print("Enter an output filename:");
        String name = s.nextLine();
        if (!name.endsWith(".csv")) {
            name += ".csv";
        }
        new DumpTopicsToCsvLong(TevaUtils.getCommunityModelFromFile(f), new FileOutputStream(name)).write();

    }



   /**
    @ConsoleDocumentation(value = "Create a static graphml representation of a community evolution file")
    public void runCreateGraphMLCommunityGraph() throws JAXBException, IOException {
        CommunityModel model = TevaUtils.getCommunityModelFromFile(U.getAnyFile("Select community file", "."));


        boolean informs = false;
        boolean spawns = false;
        System.out.print("Include informs links (y/n)? ");
        String response = s.nextLine();
        if ("y".equals(response.trim())) {
            informs = true;
        }
        System.out.print("Include spawns links (y/n)? ");
        response = s.nextLine();
        if ("y".equals(response.trim())) {
            spawns = true;
        }
        createCommunityGraphMLGraph(model, informs, spawns);
    }
    **/
   /**
    private void createCommunityGraphMLGraph(CommunityModel model, boolean informs, boolean spawns) throws JAXBException, IOException {
        UndirectedJungNetwork graph = Utils.createCommunityGraph(jaxb, spawns, true, informs);
        Utils.addDrainageScoresForCommunityGraph(graph);
        String filename = jaxb.getCorpusName() + ".static.graphml";
        Utils.writeGraphML(graph, Utils.mapify("Size", 0, "Intensity", 0, "Age", 0, "CommunityId", 0, "Centrality", 0), filename);
    }
     **/

}
