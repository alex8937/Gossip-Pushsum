# Gossip-Pushsum

Dissemination model:
1. Flood + Time to live;  
Con: Information retransimission 
2. Tree-based; 
Pro: Minimum number of message transimission for balanced tree;
Con: Not robust enough
3. Gossip network (Randomization algorithm: worst case -> average case);
Pro:  i. Asymptoically as efficient as balanced tree   ii. hard to be defeated by adversary

There are two versions of code for this project uploaded, the normal and the
bonus version. In the normal version, number of nodes, topology and algorithm are
specified as three program inputs, while an extra argument can be chosen in the bonus
version in order to set a number of nodes randomly distributed in the network into dead
nodes that do not transmit any information.

The size of largest network that this program can handle is found to be topology-
dependent only. Since the numbers of edges, i.e. the numbers of neighbors belonging to
a node are different for each topology, the size of the network is then mainly restricted
by the memory available.

Topologies given in README.pdf
