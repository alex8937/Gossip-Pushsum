# Gossip-Pushsum Notes

Dissemination model:

### A. Flooding + Time to live;  

Con: Information retransimission 

### B. Tree-based; 

Pro: Minimum number of message transimission for balanced tree;

Con: Not robust enough

### C. Gossip network (Randomization algorithm: worst case -> average case);

Pro:  i. Asymptoically as efficient as balanced tree   ii. hard to be defeated by adversary

When we need compute sum or average:

A. Flooding not working due to transimission

B. Tree affected by its robustness

C. Pushsum:  

weight: w_i(t = 0) = all 1s for sum/ one 1 rest 0s for average

value: s_i(t = 0)

Evolve Rules:

s_i(t+1) = 1/2 * s_i(t) + sum(1/2 * s_j(t))     j exists for node i receiving info from node j

Result:

lim(t->oo) [s_i(t) / w_i(t)] = average or sum depending on w_i initialization.  

Convergence time: O(logN)

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
