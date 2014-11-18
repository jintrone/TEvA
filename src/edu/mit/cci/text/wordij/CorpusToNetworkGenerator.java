package edu.mit.cci.text.wordij;

import edu.mit.cci.sna.Network;
import edu.mit.cci.teva.util.NetWriterStrategy;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.windowing.Windowable;

import java.io.File;
import java.util.List;

/**
 * Created by josh on 7/26/14.
 */
public interface CorpusToNetworkGenerator<T extends Windowable>  {
    List<File> analyzeToFiles(NetWriterStrategy strategy);

    List<Network> analyzeToMemory();
}
