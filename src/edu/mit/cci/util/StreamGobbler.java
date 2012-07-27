package edu.mit.cci.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: jintrone
 * Date: 6/17/11
 * Time: 7:42 PM
 */
public class StreamGobbler extends Thread {

    InputStream is;
    String type;
    public static Logger log = Logger.getLogger(StreamGobbler.class);


    public StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                log.debug(type + ">" + line);
            } catch (IOException ioe)
              {
                log.error("Problem reading console stream",ioe);
              }
    }
}
