package cgcp.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class represents a set of must-link constraints.
 *
 * @author Hoang Tran
 */
public class MLConstraint extends Constraint {

    /**
     * Instantiate a new Constraints
     *
     * @param nNodes		the number of nodes
     * @param nCons			the number of constraints
     * @param pointers		the pointers to adjacency and penalty list
     * @param adjList		the adjacent list
     * @param penList		the penalty list
     * @param totInPenalty	the total inner penalty list
     */
    public MLConstraint(int nNodes, int nCons, int[] pointers, int[] adjList, double[] penList, double ttInPenalty) {
    	super(nNodes, nCons, pointers, adjList, penList, ttInPenalty);
    }

    /**
     * Instantiate a new Constraints
     *
     * @param path	the file path
     * @throws IOException in case any IO error occurs
     */
    public MLConstraint(String path) throws IOException {
    	super(path);
    }
    
    /**
     * Compute the gain when assign a node to a cluster 
     *
     * @param labels	the labels of nodes
     * @param node		the node to assign
     * @param target	the target cluster
     */
    public double computeAssignGain(int[] labels, int node, int cluster) {
    	double gain = 0;
		for (int p = pointers[node]; p < pointers[node + 1]; p++) {
			int u = adjList[p];
			double pen = penList[p];
			if (labels[u] == cluster) 
				gain -= pen;
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
				gain += pen;
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
				gain += pen;
			if (labels[u] == target) 
				gain -= pen;
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
					gain += penalty;
				if (labels[u] == target) 
					gain -= penalty;
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
    			if (u < v && labels[u] != labels[v]) {
        			penalty += penList[p];
        		}
    		}
    	}
    	return penalty;
    }
    
    /**
     * Produce coarse must-link constraint
     *
     * @param nClusters		the number of clusters
     * @param labels		the labels of nodes
     * @return the coarse constraint
     */
    public MLConstraint productCoarserConstraint(int nClusters, int[] labels) {
    	int nCrsNodes = nClusters;
		int nCrsEdges = 0;
		double totlInMLPenalty = totInPenalty;
		ArrayList<HashMap<Integer, Double>> mlAdjArrList = new ArrayList<HashMap<Integer, Double>>();
		for (int i = 0; i < nCrsNodes; i++) mlAdjArrList.add(new HashMap<Integer, Double>()); 
		for (int u = 0; u < nNodes; u++) {
			for (int p = pointers[u]; p < pointers[u + 1]; p++) {
				int v = adjList[p];
				if (u < v) {
					double penalty = penList[p];
					int node1 = labels[u];
					int node2 = labels[v];
					if (node1 != node2) {
						if (!mlAdjArrList.get(node1).containsKey(node2)) {
							mlAdjArrList.get(node1).put(node2, penalty);
							mlAdjArrList.get(node2).put(node1, penalty);
							nCrsEdges++;
						} else {
							double value = mlAdjArrList.get(node1).get(node2);
							mlAdjArrList.get(node1).replace(node2, value + penalty);
							mlAdjArrList.get(node2).replace(node1, value + penalty);
						}
					} else {
						totlInMLPenalty += penalty;
					}
				}
			}
		}
		int[] mlAdjPointer = new int[nCrsNodes + 1];
		int[] mlAdjList = new int[nCrsEdges * 2];
		double[] mlPntList = new double[nCrsEdges * 2];
		int mlPointer = 0;
		for (int u = 0; u < nCrsNodes; u++) {
    		mlAdjPointer[u] = mlPointer;
    		for (Entry<Integer, Double> e : mlAdjArrList.get(u).entrySet()) {
    			int v = e.getKey();
    			double penalty = e.getValue();
        		mlAdjList[mlPointer] = v;
        		mlPntList[mlPointer] = penalty;
        		mlPointer++;
        	}
    	}
    	mlAdjPointer[nCrsNodes] = mlPointer;
    	if (mlPointer != nCrsEdges * 2) System.err.println("nCLEdges is wrong!");
    	
    	return new MLConstraint(nCrsNodes, nCrsEdges, mlAdjPointer, mlAdjList, mlPntList, totlInMLPenalty);
    }
}