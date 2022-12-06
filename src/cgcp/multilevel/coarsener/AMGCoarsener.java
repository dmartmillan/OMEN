package cgcp.multilevel.coarsener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import cgcp.model.CLConstraint;
import cgcp.model.Graph;
import cgcp.model.MLConstraint;
import cgcp.model.Problem;

/**
 * This class is a Algebraic Multigrid Coarsening implementation.
 *
 * @author Hoang Tran, Mohit Mohta.
 */
public class AMGCoarsener extends Coarsener {

	/***
     * Threshold V of Seed selection in AMG
     */
	private double thresV = 2;
	
	/***
     * Threshold Q of Seed selection in AMG
     */
	private double thresQ = 0.5;
	
	/**
     * Instantiate a new HEMCoarsener
     *
     * @param random	the random number generator
     */
	public AMGCoarsener(Random random) {
		super(random);
	}
	
	/**
     * Instantiate a new HEMCoarsener
     *
     * @param random	the random number generator
     * @param thresV	the threshold V
     * @param thresQ	the threshold Q
     */
	public AMGCoarsener(Random random, double thresV, double thresQ) {
		super(random);
		
		this.thresV = thresV;
		this.thresQ = thresQ;
	}
	
	/**
     * Perform the coarsening and return the coarse graph
     *
     * @param problem	the original problem
     * @return the coarsening solution
     */
	@Override
	public CoarseningInfo coarsen(Problem problem) {
		Graph graph = problem.graph;
		CLConstraint clCon = problem.clCon;
		MLConstraint mlCon = problem.mlCon;
		
		// create node and edge weighs
		double[] dList = new double[graph.nEdges * 2];
		double[] cList = new double[graph.nEdges * 2];

		// embed cannot-link and must-link weights
		for (int u = 0; u < graph.nNodes; u++)
			for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
				int v = graph.adjList[p];			
				double clPen = 0;
				double mlPen = 0;
				for (int pp = clCon.pointers[u]; pp < clCon.pointers[u + 1]; pp++) {
					if (clCon.adjList[pp] == v) {
						clPen += clCon.penList[pp];
						break;
					}
				}
				for (int pp = mlCon.pointers[u]; pp < mlCon.pointers[u + 1]; pp++) {
					if (mlCon.adjList[pp] == v) {
						mlPen -= mlCon.penList[pp];
						break;
					}
				}
				dList[p] = problem.gamma * clPen / clCon.maxPenalty + (1 - problem.gamma) * mlPen / mlCon.maxPenalty;
			}
			
		// normalize dList
		double min = Double.MAX_VALUE;
		for (int p = 0; p < dList.length; p++)
			if (dList[p] < min) min = dList[p];
		if (min < 0)
			for (int p = 0; p < dList.length; p++)
				dList[p] = dList[p] - min;
		
		// compute cList
		for (int p = 0; p < dList.length; p++)
			cList[p] = 1 / (1 + dList[p]);
		
		// produce the collapsing list by AMG method
		// step 1: seed selection
		double[] C = new double[graph.nNodes];
		for (int u = 0; u < graph.nNodes; u++)
			for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++)
				C[u] += cList[p];
		
		double[] V = new double[graph.nNodes];
		double total = 0;
		for (int u = 0; u < graph.nNodes; u++) {
			V[u] = 1;
			for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
				int v = graph.adjList[p];
				V[u] += 1 * cList[p] / C[v];
			}
			total += V[u];
		}
		double avg = total / graph.nNodes;
		
		boolean[] seeds = new boolean[graph.nNodes];
		for (int u = 0; u < graph.nNodes; u++)
			if (V[u] > thresV * avg) seeds[u] = true;
		for (int u = 0; u < graph.nNodes; u++)
			if (!seeds[u]) {
				double totDist = 0;
				for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
					int v = graph.adjList[p];
					if (seeds[v]) totDist += cList[p];
				}
				if (totDist / C[u] < thresQ) seeds[u] = true;
			}
		
		// step 2: coarse nodes
		int[] nodeMap = new int[graph.nNodes];
		Arrays.fill(nodeMap, -1);
		int crsNodeId = -1;
		ArrayList<Integer> vtxList = new ArrayList<Integer>();
		for (int u = 0; u < graph.nNodes; u++)
			if (seeds[u]) nodeMap[u] = ++crsNodeId;
			else vtxList.add(u);
		
		for (int u = 0; u < graph.nNodes; u++) {
			if (!seeds[u]) {
				// pick the adjacent node whose weight is largest
				int maxP = -1;
				for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
					if (seeds[graph.adjList[p]]) {
						if (maxP == -1 || cList[p] > cList[maxP]) maxP = p;
					}
				}
			
				if (maxP != -1) {
					int v = graph.adjList[maxP];
					nodeMap[u] = nodeMap[v];
				} else {
					nodeMap[u] = ++crsNodeId;
				}
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
        return "AMG coarsener";
    }
	
}