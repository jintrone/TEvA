BACKGROUND
**********
TEvA is an approach to tracing the evolution of topics (where topics are defined as something like a cluster of frequently co-occurring words) across an online conversation.  It produces a temporal trace of topics as they evolve, merge, and split.  

Algorithmically, TEvA is a mashup of a community evolution algorithm [1] and an approach to generating weighted networks from text corpora [2].  It produces something like a TextFlow analysis of an online chat forum [3], and affords measures of topic convergence and divergence. A demonstration of its application can be found in [4], 

CODE
****
This software framework is intended as a reusable, extensible library.  It depends upon an external implementation of the underlying clique percolation algorithm, and provides adapters for two such implementations.

The preferred method is a fast, open source implementation, described in [5] and available for download at http://sourceforge.net/p/cosparallel/wiki/Home/.  Note that this distribution requires that you compile two different executables, the ```maximal_cliques``` procedure which finds maximal cliques in a network, and the ```cos``` procedure which extracts k-clique communities for various sizes of k.

The system can also use CFinder for the underlying clique percolation algorithm, which is a proprietary application developed by the inventors of CPM (see [1]), and is available for educational use at http://cfinder.org.  CFinder is pure java, but only available via the command line and is somewhat slower than the COS procedure.

TEvA is licensed under the MIT License.  Use it however you want.  Citations are always appreciated (see [4]). :-)

USE
***
The algorithm requires an instance of edu.mit.cci.teva.engine.TevaParameters properties file and an instance of a Conversation (edu.mit.cci.teva.model.Conversation).

TevaParameters can be loaded from a properties file, (see ./resources/teva.default.properties). Note that TevaParameters refers to a stopword list and replacement list; default versions of these are available in the ./resources directory, but these should *not* be used for real analyses.

Clients must provide classes that implement Conversation and related entities.

There is a sample application that can be run using the class edu.mit.cci.teva.example.RunSampleCsv.  The application builds an adapter on-top of a CSV file (see edu.mit.cci.teva.adapters.csv.CsvBasedConversation), and runs the algorithm on this adapter.

Without parameters, the application will run using the sample corpus included with the distribution (./resource/sampledata/MM15.csv). The application also accepts parameters, as follows, which will allow you to use sample data of your choice:

       RunSampleCsv [-p<properties_file>] [-f<input_csv>]

The default application uses ```cos``` algorithm; please see above regarding installation.

The sample application is provided as an example only, and I recommend using a database for persistence rather than a CSV.


REFERENCES
**********
[1] Palla, G., Barabasi, A.-L., and Vicsek, T. Quantifying social group evolution. Nature 446, 7136 (2007), 664–667.

[2] Danowski, J. WORDij: A word-pair approach to information retrieval. NIST special publication, 500207 (1993), 131–136.

[3] TextFlow: Towards Better Understanding of Evolving Topics in Text. Visualization and Computer Graphics, IEEE Transactions on 17, 12 (2011), 2412–2421.

[4] Introne, J. and Drecher, M. Analyzing the Flow of Knowledge in Computer Mediated Teams. Proceedings of Computer Supported Cooperative Work, 2013. (draft available at http://www.academia.edu/2037735/Analyzing_the_Flow_of_Knowledge_in_Computer_Mediated_Teams)

[5] Enrico Gregori, Luciano Lenzini, Simone Mainardi, "Parallel k-Clique Community Detection on Large-Scale Networks," IEEE Transactions on Parallel and Distributed Systems, 31 Aug. 2012. IEEE computer Society Digital Library. IEEE Computer Society, <http://doi.ieeecomputersociety.org/10.1109/TPDS.2012.229>
