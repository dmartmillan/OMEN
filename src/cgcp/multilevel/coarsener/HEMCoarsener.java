package cgcp.multilevel.coarsener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import cgcp.model.CLConstraint;
import cgcp.model.Graph;
import cgcp.model.MLConstraint;
import cgcp.model.Problem;

/**
 * This class is a Heavy Edge Matching Coarsening implementation.
 *
 * @author Hoang Tran, Mohit Mohta.
 */
public class HEMCoarsener extends Coarsener {

	/**
     * Instantiate a new HEMCoarsener
     *
     * @param random  the random number generator
     */
	public HEMCoarsener(Random random) {
		super(random);
	}
		
	/**
     * Perform the coarsening and return the coarse graph
     *
     * @param problem  the original problem
     * @return the coarsening solution
     */
	@Override
	public CoarseningInfo coarsen(Problem problem) {
		Graph graph = problem.graph;
		CLConstraint clCon = problem.clCon;
		MLConstraint mlCon = problem.mlCon;
		
		// create node and edge weighs
		double[] edgWghList = new double[graph.nEdges * 2];
		
		// embed cannot-link and must-link weights
		for (int u = 0; u < graph.nNodes; u++)
			for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
				int v = graph.adjList[p];			
				double clPen = 0;
				double mlPen = 0;
				for (int pp = clCon.pointers[u]; pp < clCon.pointers[u + 1]; pp++) {
					if (clCon.adjList[pp] == v) {
						clPen -= clCon.penList[pp];
						break;
					}
				}
				for (int pp = mlCon.pointers[u]; pp < mlCon.pointers[u + 1]; pp++) {
					if (mlCon.adjList[pp] == v) {
						mlPen += mlCon.penList[pp];
						break;
					}
				}
				edgWghList[p] = problem.gamma * clPen / clCon.maxPenalty + (1 - problem.gamma) * mlPen / mlCon.maxPenalty;
			}

		// produce the collapsing list by HEM method
		ArrayList<Integer> vtxList = new ArrayList<Integer>();
		for (int u = 0; u < graph.nNodes; u++) vtxList.add(u);
		
		int crsNodeId = -1;
		int[] nodeMap = new int[graph.nNodes];
		Arrays.fill(nodeMap, -1);
		while (vtxList.size() > 0) {
			// pick a node randomly
			int node1 = vtxList.get(random.nextInt(vtxList.size()));
			
			// pick the adjacent node whose weight is largest
			int maxP = -1;
			for (int p = graph.pointers[node1]; p < graph.pointers[node1 + 1]; p++) {
				if (nodeMap[graph.adjList[p]] == -1) {
					if (maxP == -1 || edgWghList[p] > edgWghList[maxP]) maxP = p;
				}
			}
			
			if (maxP != -1) {
				int node2 = graph.adjList[maxP];
				
				// collapse the node and its adjacent node
				crsNodeId++;
				nodeMap[node1] = crsNodeId;
				nodeMap[node2] = crsNodeId;
				
				// remove the node and its adjacent node from node list
				vtxList.remove((Integer) node1);
				vtxList.remove((Integer) node2);
			} else {
				// collapse the node
				crsNodeId++;
				nodeMap[node1] = crsNodeId;
				
				// remove the node from node list
				vtxList.remove((Integer) node1);
			}
		}
		int nCrsNodes = crsNodeId + 1;		
		CoarseningInfo crsInfo = new CoarseningInfo(nCrsNodes, nodeMap);
		
		return crsInfo;
	}
	
	/**
     * Return the string representation of the HEMCoarsener
     *
     * @return the string representation of the HEMCoarsener
     */
	public String toString() {
        return "HEM coarsener";
    }
	
}