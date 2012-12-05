package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.Node;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.engine.CommunityFrame;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.serialization.CommunityFrameJaxbAdapter;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import org.apache.commons.collections15.Transformer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/8/12
 * Time: 3:11 PM
 */

public class TevaUtils {


    public static void serialize(Clique clique, File f) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(Clique.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);

//        edu.mit.cci.snatools.topicevolution.Utils.analyzeDrainagePatterns(c);
        FileOutputStream output = new FileOutputStream(f);
        m.marshal(clique, output);
        output.flush();
        output.close();

    }

     public static void serialize(CommunityFrame communityFrame, File f) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(CommunityFrameJaxbAdapter.class,CommunityFrame.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);

//        edu.mit.cci.snatools.topicevolution.Utils.analyzeDrainagePatterns(c);
        FileOutputStream output = new FileOutputStream(f);
        m.marshal(communityFrame, output);
        output.flush();
        output.close();

    }

    public static CommunityModel getCommunityModelFromFile(File file) throws JAXBException, FileNotFoundException {
        return deserialize(file,CommunityModel.class);
    }

    public static List<String> getCommunityRepresentation(Network c, int size) {
        UndirectedJungNetwork graph = new UndirectedJungNetwork();
        List<String> s = new ArrayList<String>();

        for (Edge e : c.getEdges()) {
            graph.add(e);
        }

        final EigenvectorCentrality<Node, Edge> ev = new EigenvectorCentrality<Node, Edge>(graph, new Transformer<Edge, Float>() {
            public Float transform(Edge e) {
                return e.getWeight();
            }
        });
        ev.evaluate();
        List<Node> sortedlist = new ArrayList<Node>(graph.getVertices());
        Collections.sort(sortedlist, new Comparator<Node>() {
            public int compare(Node defaultJungNode, Node defaultJungNode1) {
                double score1 = ev.getVertexScore(defaultJungNode);
                double score2 = ev.getVertexScore(defaultJungNode1);
                if (score1 < score2) return 1;
                else if (score2 == score1) return 0;
                else return -1;

            }
        }
        );

        for (int i = 0; i < Math.min(size, sortedlist.size()); i++) {
            s.add(sortedlist.get(i).getLabel());
        }
        return s;


    }



    public static Clique deserializeClique(File f) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(Clique.class);
        Clique x = (Clique) context.createUnmarshaller().unmarshal(new FileInputStream(f));
        return x;
    }

     public static<T> T deserialize(File f, Class... support) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(support);
        Unmarshaller m =  context.createUnmarshaller();
         m.setEventHandler(new DefaultValidationEventHandler());
        T x = (T) m.unmarshal(new FileInputStream(f));
        return x;
    }

    public static<T> void serialize(File f, T obj, Class... support) throws JAXBException, IOException {
       JAXBContext context = JAXBContext.newInstance(support);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);
        FileOutputStream output = new FileOutputStream(f);
        m.marshal(obj, output);
        output.flush();
        output.close();

    }


}
