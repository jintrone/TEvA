package edu.mit.cci.teva.util;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.teva.engine.CommunityFrame;
import edu.mit.cci.teva.serialization.CommunityFrameJaxbAdapter;

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
