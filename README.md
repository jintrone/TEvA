[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/5bef61b67c3996417b51f2a055b9cdad "githalytics.com")](http://githalytics.com/jintrone/TEvA)

BACKGROUND
**********
TEvA is an approach to tracing the evolution of topics (where topics are defined as something like a cluster of frequently co-occurring words) across an online conversation.  It produces a temporal trace of topics as they evolve, merge, and split.  

Algorithmically, TEvA is a mashup of a community evolution algorithm [1] and an approach to generating weighted networks from text corpora [2].  It produces something like a TextFlow analysis of an online chat forum [3], and affords measures of topic convergence and divergence. A demonstration of its application can be found in [4], 

CODE
****
This software framework is intended as a reusable, extensible library.  It currently depends upon CFinder for the underlying clique percolation algorithm, a proprietary application available for educational use at http://cfinder.org.  In the near future, I will be switching to a faster, open-source implementation of the algorithm, described in [5] and available for download at http://sourceforge.net/p/cosparallel/wiki/Home/.

TEvA is licensed under the MIT License.  Use it however you want.  Citations are always appreciated (see [4]). :-)

RUNNING
*******
There is a sample application that can be run using the class edu.mit.cci.teva.example.RunSampleCsv.  As the name implies, it builds an adapter on-top of a CSV file (see edu.mit.cci.teva.adapters.csv.CsvBasedConversation) which is available in the resources/sample directory.  This code is provided as an example only, and I recommend using a database for persistence rather than a CSV. 


REFERENCES
**********
[1] Palla, G., Barabasi, A.-L., and Vicsek, T. Quantifying social group evolution. Nature 446, 7136 (2007), 664–667.

[2] Danowski, J. WORDij: A word-pair approach to information retrieval. NIST special publication, 500207 (1993), 131–136.

[3] TextFlow: Towards Better Understanding of Evolving Topics in Text. Visualization and Computer Graphics, IEEE Transactions on 17, 12 (2011), 2412–2421.

[4] Introne, J. and Drecher, M. Analyzing the Flow of Knowledge in Computer Mediated Teams. Proceedings of Computer Supported Cooperative Work, 2013. (draft available at http://www.academia.edu/2037735/Analyzing_the_Flow_of_Knowledge_in_Computer_Mediated_Teams)

[5] Enrico Gregori, Luciano Lenzini, Simone Mainardi, "Parallel k-Clique Community Detection on Large-Scale Networks," IEEE Transactions on Parallel and Distributed Systems, 31 Aug. 2012. IEEE computer Society Digital Library. IEEE Computer Society, <http://doi.ieeecomputersociety.org/10.1109/TPDS.2012.229>
