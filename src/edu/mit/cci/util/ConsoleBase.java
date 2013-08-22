package edu.mit.cci.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jintrone
 * Date: 7/13/12
 * Time: 9:09 AM
 */
public class ConsoleBase {
    protected Scanner s;
    Map<Integer, MethodRunner> methodRunnerMap = new HashMap<Integer, MethodRunner>();
    String docstring = "";


    public void start() {

        s = new Scanner(System.in);
        int i = 1;
        for (Method m : getClass().getMethods()) {
            if (m.getName().startsWith("run")) {
                ConsoleDocumentation a = m.getAnnotation(ConsoleDocumentation.class);
                methodRunnerMap.put(i++, new MethodRunner(m, a.value()));
            }

        }
        Method e = null;
        try {
            e = getClass().getMethod("exit");

        } catch (NoSuchMethodException ex) {
            //this shouldn't happen
            throw (new RuntimeException("Couldn't locate exit method"));
        }


        methodRunnerMap.put(i, new MethodRunner(e, e.getAnnotation(ConsoleDocumentation.class).value()));
        buildDocString();
        waitForInput();
    }


    public void buildDocString() {
        for (int i = 1; i <= methodRunnerMap.size(); i++) {
            docstring += "\n[" + i + "] " + methodRunnerMap.get(i).documentation();

        }


    }

    public String docstring() {
        return docstring;
    }

    @ConsoleDocumentation(value = "Exit.")
    public void exit() {
        System.out.println("Bye!");
        System.exit(1);
    }

    public void handleError(Throwable e) {
        System.out.println("Encountered error while running task:" + e.getMessage());
        e.printStackTrace(System.out);
        System.out.println("\n\n");
    }

    public void waitForInput() {
        Pattern p = Pattern.compile(".*?(\\d+).*$");

        System.out.println(docstring());
        while (s.hasNext()) {
            String l = s.nextLine();
            Matcher m = p.matcher(l);

            if (m.matches()) {
                int i = Integer.parseInt(m.group(1));

                MethodRunner runner = methodRunnerMap.get(i);
                if (runner != null) {
                    try {
                        runner.run();
                    } catch (Throwable e) {
                        handleError(e);
                    }

                }
            } else {
                System.out.println("Unrecognized");
            }

            System.out.println(docstring());
        }
    }

    public String askUser(String question) {
        System.out.print(question + " ");
        return s.nextLine();
    }

    public String askUser(String question, String def) {
        System.out.print(question + " ["+def+"]");
        String result = s.nextLine();
        if (result.trim().isEmpty()) {
            return def;
        } else {
            return result;
        }
    }

    protected class MethodRunner {


        private Method m;
        private String doc;

        public MethodRunner(Method m, String doc) {
            this.m = m;
            this.doc = doc;
        }

        public String documentation() {
            return doc;
        }

        public void run() throws InvocationTargetException, IllegalAccessException {
            m.invoke(ConsoleBase.this);
        }
    }
}
