package cgcp.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class represents a set of cannot-link constraints.
 *
 * @author Hoang Tran
 */
public class CLConstraint extends Constraint {

    /**
     * Instantiate a new Constraints
     *
     * @param nNodes		the number of nodes
     * @param nCons			the number of constraints
     * @param pointers		the pointers to adjacency and penalty list
     * @param adjList		the adjacent list
     * @param penList		the penalty list
     * @param totInPenalty	the total inner penalty
     */
    public CLConstraint(int nNodes, int nCons, int[] pointers, int[] adjList, double[] penList, double totInPenalty) {
    	super(nNodes, nCons, pointers, adjList, penList, totInPenalty);
    }

    /**
     * Instantiate a new Constraints
     *
     * @param path	the file path
     * @throws IOException in case any IO error occurs
     */
    public CLConstraint(String path) throws IOException {
    	super(path);
    }
    
    /**
     * Compute the gain when assign a node to a cluster 
     *
     * @param labels	the labels of nodes
     * @param node		the node to assign
     * @param target	the target cluster
     * @return the assign gain
     */
    public double computeAssignGain(int[] labels, int node, int target) {
    	double gain = 0;
		for (int p = pointers[node]; p < pointers[node + 1]; p++) {
			int u = adjList[p];
			double pen = penList[p];
			if (labels[u] == target) 
				gain += pen;
		}
		
		return gain;
    }
    
    /**
     * Compute the gain when remove a node from its cluster 
     *
     * @param labels	the labels of nodes
     * @param node		the node to remove
     * @return the remove gain
     */
    public double computeRemoveGain(int[] labels, int node) {
    	int source = labels[node];
    	double gain = 0;
		for (int p = pointers[node]; p < pointers[node + 1]; p++) {
			int u = adjList[p];
			double pen = penList[p];
			if (labels[u] == source) 
				gain -= pen;
		}
		
		return gain;
    }
    
    /**
     * Compute the gain when move a node to new cluster 
     *
     * @param labels	the labels of nodes
     * @param node		the node to move
     * @param target	the target cluster
     * @return the move gain
     */
    public double computeMoveGain(int[] labels, int node, int target) {
    	int source = labels[node];
    	double gain = 0;
		for (int p = pointers[node]; p < pointers[node + 1]; p++) {
			int u = adjList[p];
			double pen = penList[p];
			if (labels[u] == source) 
				gain -= pen;
			if (labels[u] == target) 
				gain += pen;
		}
		
		return gain;
    }
    
    /**
     * Compute the gain when move a set of node to new cluster 
     *
     * @param labels	the labels of nodes
     * @param nodes		the set of nodes to move
     * @param source	the source cluster
     * @param target	the target cluster
     * @return the move gain
     */
    public double computeMoveGain(int[] labels, ArrayList<Integer> nodes, int source, int target) {
    	double gain = 0;
		for (int node : nodes) {
			for (int p = pointers[node]; p < pointers[node + 1]; p++) {
				int u = adjList[p];
				double penalty = penList[p];
				if (labels[u] == source) 
					gain -= penalty;
				if (labels[u] == target) 
					gain += penalty;
			}
			labels[node] = target;
		}
		for (int node : nodes) labels[node] = source;
		
    	return gain;
	}
    
    /**
     * Compute the constraint violation penalty 
     *
     * @param labels	the labels of nodes
     * @return the violation penalty
     */
    public double computeViolationPenalty(int[] labels) {
    	double penalty = 0;
    	for (int u = 0; u < nNodes; u++) {
    		for (int p = pointers[u]; p < pointers[u + 1]; p++) {
    			int v = adjList[p];
    			if (u < v && labels[u] == labels[v]) {
        			penalty += penList[p];
        		}
    		}
    	}
    	
    	return penalty;
    }
    
    /**
     * Produce coarse cannot-link constraint 
     *
     * @param nClusters		the number of clusters
     * @param labels		the labels of nodes
     * @return the coarse constraint
     */
    public CLConstraint produceCoarseConstraint(int nClusters, int[] labels) {
    	int nCrsNodes = nClusters;
		int nCrsEdges = 0;
		double totlInCLPenalty = totInPenalty;
		ArrayList<HashMap<Integer, Double>> clAdjArrList = new ArrayList<HashMap<Integer, Double>>();
		for (int i = 0; i < nCrsNodes; i++) clAdjArrList.add(new HashMap<Integer, Double>()); 
		for (int u = 0; u < nNodes; u++) {
			for (int p = pointers[u]; p < pointers[u + 1]; p++) {
				int v = adjList[p];
				if (u < v) {
					double penalty = penList[p];
					int node1 = labels[u];
					int node2 = labels[v];
					if (node1 != node2) {
						if (!clAdjArrList.get(node1).containsKey(node2)) {
							clAdjArrList.get(node1).put(node2, penalty);
							clAdjArrList.get(node2).put(node1, penalty);
							nCrsEdges++;
						} else {
							double value = clAdjArrList.get(node1).get(node2);
							clAdjArrList.get(node1).replace(node2, value + penalty);
							clAdjArrList.get(node2).replace(node1, value + penalty);
						}
					} else {
						totlInCLPenalty += penalty;
					}
				}
			}
		}
		int[] clAdjPointer = new int[nCrsNodes + 1];
		int[] clAdjList = new int[nCrsEdges * 2];
		double[] clPntList = new double[nCrsEdges * 2];
		int clPointer = 0;
		for (int u = 0; u < nCrsNodes; u++) {
    		clAdjPointer[u] = clPointer;
    		for (Entry<Integer, Double> e : clAdjArrList.get(u).entrySet()) {
    			int v = e.getKey();
    			double penalty = e.getValue();
        		clAdjList[clPointer] = v;
        		clPntList[clPointer] = penalty;
        		clPointer++;
        	}
    	}
    	clAdjPointer[nCrsNodes] = clPointer;
    	if (clPointer != nCrsEdges * 2) System.err.println("nCLEdges is wrong!");
    	
    	return new CLConstraint(nCrsNodes, nCrsEdges, clAdjPointer, clAdjList, clPntList, totlInCLPenalty);
    }
}