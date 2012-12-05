package edu.mit.cci.teva.analysis;

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
        File f = U.getAnyFile(message,props.getProperty("last_file"));
        if (f!=null) updateProperties(f);
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


}
