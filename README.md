BACKGROUND
**********

TEvA is an approach to tracing the evolution of topics (where topics are defined as something like a cluster of frequently co-occurring words) across an online conversation.  It produces a temporal trace of topics as they evolve, merge, and split over time.  

Algorithmically, TEvA is a mashup of a community evolution algorithm [1] and an approach to generating weighted networks from text corpora [2].  It produces something like a TextFlow analysis of an online chat forum [3], and affords measures of topic convergence and divergence. A demonstration of its application can be found in [4], 


[1] Palla, G., Barabasi, A.-L., and Vicsek, T. Quantifying social group evolution. Nature 446, 7136 (2007), 664–667.
[2] Danowski, J. WORDij: A word-pair approach to information retrieval. NIST special publication, 500207 (1993), 131–136.
[3] TextFlow: Towards Better Understanding of Evolving Topics in Text. Visualization and Computer Graphics, IEEE Transactions on 17, 12 (2011), 2412–2421.
[4] Introne, J. and Drecher, M. Analyzing the Flow of Knowledge in Computer Mediated Teams. Proceedings of Computer Supported Cooperative Work, 2013.

CODE
****

This software framework is intended as a reusable, extensible library.  It currently depends upon CFinder, a proprietary application available for educational use at http://cfinder.org, for the underlying clique percolation algorithm.  In the near future, I will be switching to a faster, open-source implementation of the algorithm.

TEvA is licensed under the MIT License.  Use it however you want.  Citations are always appreciated. :-)