package edu.mit.cci.sna.jung;

import edu.mit.cci.sna.impl.EdgeImpl;
import edu.mit.cci.sna.impl.NodeImpl;

import edu.mit.cci.util.UnicodeReader;
import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.io.PajekNetWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 4/21/11
 * Time: 10:53 AM
 */
public class Loader {


    public static DirectedSparseMultigraph<NodeImpl, EdgeImpl> loadGraph(File file) throws IOException {
        Reader fileReader = new UnicodeReader(new FileInputStream(file), "UTF-8");
        DirectedSparseMultigraph<NodeImpl, EdgeImpl> graph = new DirectedSparseMultigraph<NodeImpl, EdgeImpl>();
        PajekNetReader<DirectedSparseMultigraph<NodeImpl, EdgeImpl>, NodeImpl, EdgeImpl> graphreader = new PajekNetReader<DirectedSparseMultigraph<NodeImpl, EdgeImpl>, NodeImpl, EdgeImpl>(NodeImpl.getFactory(), EdgeImpl.getFactory());
        graphreader.setEdgeWeightTransformer(new MapSettableTransformer<EdgeImpl, Number>(new HashMap<EdgeImpl, Number>()));
        graphreader.setVertexLabeller(new MapSettableTransformer<NodeImpl, String>(new HashMap<NodeImpl, String>()));
        graphreader.load(fileReader, graph);

        //TODO this is a problem
        for (NodeImpl node : graph.getVertices()) {
            node.setLabel(graphreader.getVertexLabeller().transform(node));
        }
        for (EdgeImpl edge : graph.getEdges()) {
            edge.setWeight(graphreader.getEdgeWeightTransformer().transform(edge).floatValue());
        }
        return graph;

    }

     public static void writeGraph(Graph<NodeImpl,EdgeImpl> graph, OutputStream stream, boolean labels, boolean weights) throws IOException {
        PajekNetWriter<NodeImpl, EdgeImpl> writer = new PajekNetWriter<NodeImpl, EdgeImpl>();
        OutputStreamWriter os = new OutputStreamWriter(stream);
         Map<NodeImpl,String> labelmap = new HashMap<NodeImpl, String>();
         Map<EdgeImpl,Number> weightmap = new HashMap<EdgeImpl,Number>();
         if (labels) {
             for (NodeImpl n:graph.getVertices()) {
                 labelmap.put(n,n.getLabel());
             }
         }

         if (weights) {
             for (EdgeImpl e:graph.getEdges()) {
                 weightmap.put(e,e.getWeight());
             }
         }

        writer.save(graph,os,new MapSettableTransformer<NodeImpl, String>(labelmap),new MapSettableTransformer<EdgeImpl, Number>(weightmap));
        //os.flush();
     }
}
