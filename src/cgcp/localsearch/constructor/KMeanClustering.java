package cgcp.localsearch.constructor;

import java.util.Arrays;
import java.util.Random;

import cgcp.model.Graph;
import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 * This class contains a constructive procedure based on k-Mean clustering.
 *
 * @author Hoang Tran
 */
public class KMeanClustering extends Constructor {

	/***
     * Distance matrix
     */
	private double[][] distances;
	
	/**
     * Instantiate a new KMeanClustering
     *
     * @param proplem	the problem
     * @param random	the random number generator 
     */
	public KMeanClustering(Problem problem, Random random) {
		super(problem, random, "KM");

		int nNodes = problem.graph.nNodes;
		distances = new double[nNodes][nNodes];
		
		// compute shortest path distances
		for (int u = 0; u < nNodes; u++) {
        	Arrays.fill(distances[u], Integer.MAX_VALUE);
        	distances[u][u] = 0;
        	for (int p = problem.graph.pointers[u]; p < problem.graph.pointers[u + 1]; p++) {
        		int v = problem.graph.adjList[p];
        		distances[u][v] = 1;
        	}
        }
		
        for (int w = 0; w < nNodes; w++) {
        	for (int u = 0; u < nNodes; u++) {
        		for (int v = 0; v < nNodes; v++) {
        			if (distances[u][v] > distances[u][w] + distances[w][v]) {
        				distances[u][v] = distances[u][w] + distances[w][v];
        			}
        		}
        	}
        }
		
        // embedding constraint distances
        for (int u = 0; u < problem.graph.nNodes; u++) {
        	// embed cannot-link weights
        	for (int p = problem.clCon.pointers[u]; p < problem.clCon.pointers[u + 1]; p++) {
	        	int v = problem.clCon.adjList[p];
	        	double value = problem.gamma * problem.clCon.penList[p] / problem.clCon.maxPenalty;
	        	distances[u][v] += value;
	        }
        	
        	// embed must-link weights
        	for (int p = problem.mlCon.pointers[u]; p < problem.mlCon.pointers[u + 1]; p++) {
	        	int v = problem.mlCon.adjList[p];
	        	double value = (1 - problem.gamma) * problem.mlCon.penList[p] / problem.mlCon.maxPenalty;
	        	distances[u][v] -= value;
	        }
        }
	}

	@Override
    public Solution generate(int nClusters) {
    	Graph graph = problem.graph;
    	int nNodes = graph.nNodes;    	
    	int[] labels = new int[nNodes];
    	double cost = 0;
    	
		// Step 1: select seeds at random
    	Arrays.fill(labels, -1);
    	for (int c = 0; c < nClusters; c++) {
			int u = -1;
			do u = random.nextInt(nNodes);
			while (labels[u] != -1);
			labels[u] = c;
		}
    	
    	// Step 2: assignment step
    	cost = assignNodeToCluster(nClusters, labels);
    	int[] bestLabels = labels.clone();
    	double bestCost = cost;
    	boolean improved = false;
    	do {
    		// Step 3: update step
    		updateCentroids(nClusters, labels);
    		
    		// Step 2: assignment step
        	cost = assignNodeToCluster(nClusters, labels);
        	
        	if (cost < bestCost) {
        		bestLabels = labels.clone();
        		bestCost = cost;
        		improved = true;
        	}
        	else improved = false;
    	} while (improved);
		
    	// create solution
    	return new Solution(problem, nClusters, bestLabels);
    }
	
	private double assignNodeToCluster(int nClusters, int[] labels) {
		Graph graph = problem.graph;
		int nNodes = graph.nNodes;
		
		for (int i = 0; i < nNodes - nClusters; i++) {
			int node = -1;
			int target = -1;
			double minGain = Double.MAX_VALUE;
			for (int u = 0; u < nNodes; u++) {
				if (labels[u] != -1) continue;
				boolean[] clusters = new boolean[nClusters];
				Arrays.fill(clusters, false);
				for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
					int v = graph.adjList[p];
					if (labels[v] == -1 || clusters[labels[v]]) continue;
					double gain = problem.computeAssignGain(labels, u, labels[v]);
					if (gain < minGain) {
						node = u;
						target = labels[v];
						minGain = gain;
					}
					clusters[labels[v]] = true;
				}
			}
			// perform the move
			labels[node] = target;
		}
		
		return problem.calculateTotalPenalty(labels);
	}

	private void updateCentroids(int nClusters, int[] labels) {
		Graph graph = problem.graph;
		int nNodes = graph.nNodes;
		int[] newLabels = new int[labels.length];
		Arrays.fill(newLabels, -1);
		
		for (int c = 0; c < nClusters; c++) {
			int centroid = -1;
    		int minDist = Integer.MAX_VALUE;
    		for (int u = 0; u < nNodes; u++) {
    			if (labels[u] != c) continue;
    			int totalDist = 0;
    			for (int v = 0; v < nNodes; v++) {
        			if (labels[v] != c) continue;
        			totalDist += distances[u][v];
        		}
    			if (totalDist < minDist) {
    				centroid = u;
    				minDist = totalDist;
    			}
    		}
    		newLabels[centroid] = c;
    	}
		
		System.arraycopy(newLabels, 0, labels, 0, labels.length);
	}
	
}
