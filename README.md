# QuadtreeSwarm
A project using quadtrees to enable thousand of units interacting with each other while still maintaining high framerates.


Quadtrees are mathematical structures to enable data locality. In this example a quadtree is used to store bird like entities that behave in a swarm like fashion.
Each cell can contain up to a given number of entities (by default 64) before it get's split into four child cells. Doing this enables interaction between thousands of units without bogging down the processing rate.

The programm is writtin in Java and the visualisation is done using the libgdx framework.

You can see a video example with 2000 units of the programm on youtube (video is choppy due to the recording software)
https://www.youtube.com/watch?v=CqucReHyQMM
