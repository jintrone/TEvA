package edu.mit.cci.teva.cpm.cfinder;

import edu.mit.cci.sna.Clique;
import edu.mit.cci.sna.CliqueDecoratedNetwork;
import edu.mit.cci.sna.CliqueDecorater;
import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;
import edu.mit.cci.sna.jung.UndirectedJungNetwork;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.util.U;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jintrone
 * Date: 4/19/12
 * Time: 3:45 AM
 */
public class CFinderReader {

    private static Logger log = Logger.getLogger(CFinderReader.class);


    public static List<CliqueDecoratedNetwork> readCommunities(int cliquesize, File basedir) throws IOException, CommunityFinderException {
        File target = new File(basedir, "k=" + cliquesize);
        Map<Integer, Clique> cliques = processGlobalCliques(basedir);
        File communitiesFile = new File(target, "communities_links");
        boolean intensity = false;
        if (!communitiesFile.exists()) {
            communitiesFile = new File(target, "intensity_communities_links");
            if (!communitiesFile.exists()) {
                throw new CommunityFinderException("Could not identify communities file");

            } else {
                intensity = true;
            }
        }

        List<CliqueDecoratedNetwork> infos = new ArrayList<CliqueDecoratedNetwork>();

        BufferedReader communityReader = new BufferedReader(new FileReader(communitiesFile));
        BufferedReader cliqueReader = new BufferedReader(new FileReader(new File(target, intensity ? "intensity_communities_cliques" : "communities_cliques")));

        String line;
        List<String> links = new ArrayList<String>();
        Pattern commPattern = Pattern.compile("^-?(\\d+):$");
        CliqueDecoratedNetwork cinfo = null;
        String communityId = "";

        while ((line = communityReader.readLine()) != null) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            Matcher m = commPattern.matcher(line);
            if (m.matches()) {
                log.debug("Match line: "+line);
                if (cinfo != null) {
                    cinfo.addCliques(readCliques(communityId, cliqueReader, cliques));
                    log.debug("Adding info for community: "+communityId);
                    infos.add(cinfo);
                }
                cinfo = new CliqueDecorater(new UndirectedJungNetwork());
                communityId = m.group(1);


            } else {
                String[] n = line.trim().split(" ");
                if (cinfo == null) {
                    throw new CommunityFinderException("Error processing community file:" + communitiesFile.getAbsolutePath());
                }
                cinfo.add(new EdgeImpl(new NodeImpl(n[0]), new NodeImpl(n[1]), 1.0f, false));
            }
        }


        if (cinfo != null) {
            log.debug("Adding info for community: "+communityId);
            infos.add(cinfo);
            cinfo.addCliques(readCliques(communityId, cliqueReader, cliques));
        }
        communityReader.close();
        cliqueReader.close();
        return infos;
    }

    public static Map<Integer, Clique> processGlobalCliques(File outputdir) throws IOException {
        Map<Integer, Clique> result = new HashMap<Integer, Clique>();
        File f = new File(outputdir, "cliques");
        if (!f.exists()) {
            log.warn("Can't identify clique file");
            return null;
        }

        Pattern p = Pattern.compile("^(\\d+):(.+)$");

        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            Matcher m = p.matcher(line);
            if (m.matches()) {
                Clique c = new Clique(Integer.parseInt(m.group(1)));

                c.setNodes(Arrays.asList(U.mysplit(m.group(2), "\\s+")));

                result.put(c.getId(), c);
            }
        }
        return result;
    }

    public static List<Clique> readCliques(String id, BufferedReader reader, Map<Integer, Clique> gcliques) throws IOException {
        String line = null;
        Pattern p = Pattern.compile("^(\\d+):(.+)$");
        List<Clique> result = new ArrayList<Clique>();
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            Matcher m = p.matcher(line);
            if (m.matches()) {
                if (!m.group(1).equals(id)) {
                    log.warn("Could not identify community " + id + " in clique cliquelist; continuing");
                } else {
                    for (String cid : m.group(2).split("\\s+")) {
                        if (cid.trim().isEmpty()) continue;
                        result.add(gcliques.get(Integer.parseInt(cid)));
                    }
                    break;
                }


            }

        }
        return result;
    }


    public static void main(String[] args) throws IOException, CommunityFinderException {
        readCommunities(4, new File("/Users/jintrone/Documents/DEV/REASONTEVA/work/CFinderNetwork.TEvA.0.net_files"));
    }


}
