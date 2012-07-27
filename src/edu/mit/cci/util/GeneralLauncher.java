package edu.mit.cci.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: jintrone
 * Date: 7/27/12
 * Time: 12:10 PM
 */
public class GeneralLauncher {

    public static void main(String[] args) throws ClassNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        Map<String,String> params = new HashMap<String,String>();
        for (int i = 0;i<(args.length/2) * 2;) {
          params.put(args[i],args[++i]);
            i++;
        }

        Class target = null;
        if (!params.containsKey("-c")) {
            exit("Must specify class file to launch with -c");

        } else {
            target = Class.forName(params.remove("-c"));
        }


        Properties p = new Properties();

        if (params.containsKey("-p")) {
            File f= new File(params.remove("-p"));
            if (!f.exists()) {
              exit("Properties file "+f.getAbsolutePath()+" could not be found");
            } else {
                p.load(new FileReader(f));
            }

        } else {
           for (Map.Entry<String,String> ent:params.entrySet()) {
               if (ent.getKey().startsWith("-")) {
                   p.setProperty(ent.getKey().substring(1),ent.getValue());
               } else {
                   p.setProperty(ent.getKey(),ent.getValue());
               }
           }

        }

        Constructor c = target.getDeclaredConstructor(Properties.class);
        c.newInstance(p);

    }

    public static void exit(String msg) {
        System.err.println(msg);
        System.exit(-1);

    }

}
