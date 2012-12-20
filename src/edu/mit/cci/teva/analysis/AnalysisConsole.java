package edu.mit.cci.teva.analysis;

import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.engine.CommunityModel;
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
import java.util.Properties;

/**
 * User: jintrone
 * Date: 10/12/12
 * Time: 9:33 AM
 */
public class AnalysisConsole extends ConsoleBase {

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
        File f = U.getAnyFile(message, props.getProperty("last_file"));
        if (f != null) updateProperties(f);
        return f;
    }


    public static void main(String[] args) throws IOException {
        new AnalysisConsole();
    }


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
