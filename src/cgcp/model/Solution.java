package cgcp.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import cgcp.util.Util;

/**
 * This class represents a Solution of the Constrained Graph Clustering Problem.
 *
 * @author Hoang Tran
 */
public class Solution {

	/***
     * The problem considered
     */
    public final Problem problem;
    
    /***
     * The number of clusters
     */
    private int nClusters;
    
    /***
     * The labels of nodes
     * labels[node]
     */
    private final int[] labels;
    
    /***
     * Size of each cluster
     * cSizes[cluster]
     */
    private final int[] cSizes;
    
    /***
     * Variables used for finding articulation points 
     */
    private final boolean[] APs;
    
    /***
     * The total penalty score for violating constraints 
     */
    private double totPenalty;
    
    /**
     * Instantiate a new Solution
     *
     * @param problem  the problem considered
     */
    public Solution(Problem problem, int nClusters, int[] labels) {
        this.problem = problem;
        this.nClusters = nClusters;
        this.labels = labels;
        this.cSizes = computeClusterSizes(nClusters, labels);
        this.APs = computeAPs(problem.graph, nClusters, labels);
        this.totPenalty = problem.calculateTotalPenalty(labels);
    }

    /**
     * Private constructor used for cloning
     *
     * @param solution  the solution to copy from
     */
    private Solution(Solution solution) {
        this.problem = solution.problem;
        this.nClusters = solution.nClusters;
        this.labels = solution.labels.clone();
        this.cSizes = solution.cSizes.clone();
        this.APs = solution.APs.clone();
        this.totPenalty = solution.totPenalty;
    }
    
    /**
     * Create and return a copy of this solution
     */
    public Solution clone() {
        return new Solution(this);
    }
    
    /**
     * Get the number of clusters
     */
    public int nClusters() {
    	return nClusters;
    }
    
    /**
     * Get the length of solution
     */
    public int length() {
    	return labels.length;
    }
    
    /**
     * Get the number of clusters
     */
    public int getLabel(int node) {
    	return labels[node];
    }
    
    /**
     * Set new label for a node
     *
     * @param node		the node
     * @param target	new label
     */
    public void setLabel(int node, int target) {
    	int source = labels[node];
    	labels[node] = target;
    	cSizes[source] -= 1;
    	cSizes[target] += 1;
    }
    
    /**
     * Set new label for a AP node and a set of nodes moved together
     *
     * @param node		the node
     * @param target	new label
     * @param random	the random number generator
     */
    public void setAPLabel(int AP, int target, Random random) {
    	int size = 0;
    	int source = labels[AP]; 
    	labels[AP] = target;
    	size++;
    	
    	int[] cLabels = new int[labels.length];
		int nComs = problem.graph.getComponents(labels, source, cLabels);
		int c = random.nextInt(nComs);
		for (int u = 0; u < labels.length; u++)
			if (cLabels[u] != -1 && cLabels[u] != c) {
				labels[u] = target;
				size++;
			}
		cSizes[source] -= size;
    	cSizes[target] += size;
    }
    
    /**
     * Set new label for a AP node and a set of nodes moved together
     *
     * @param node		the node
     * @param target	new label
     * @param random	the random number generator
     */
    public void setAPLabel2(int AP, int target, Random random) {
    	int size = 0;
    	int source = labels[AP]; 
    	labels[AP] = target;
    	size++;
    	
    	int[] cLabels = new int[labels.length];
		int nComs = problem.graph.getComponents(labels, source, cLabels);
		int minCom = -1;
		double minGain = Double.MAX_VALUE;
		for (int c = 0; c < nComs; c++) {
			double gain = 0;
			ArrayList<Integer> nodes = new ArrayList<Integer>();
			for (int u = 0; u < labels.length; u++)
				if (cLabels[u] != -1 && cLabels[u] != c) {
					gain += getMoveGain(u, target);
					labels[u] = target;
					nodes.add(u);
				}
			if (gain < minGain) {
				minCom = c;
				minGain = gain;
			}
			for (int u : nodes) labels[u] = source;
		}
		
		for (int u = 0; u < labels.length; u++)
			if (cLabels[u] != -1 && cLabels[u] != minCom) {
				labels[u] = target;
				size++;
			}
		cSizes[source] -= size;
    	cSizes[target] += size;
    }
    
    /**
     * Clone and return the labels of nodes
     * 
     * @return the cloned labels of nodes
     */
    public int[] cloneLabels() {
    	return labels.clone();
    }

    /**
     * Get the size of a cluster
     * 
     * @param cluster	the cluster
     */
    public int getClusterSize(int cluster) {
    	return cSizes[cluster];
    }
    
    /**
     * Get the size of the cluster to which the node belong
     * 
     * @param	the cluster
     */
    public int getClusterSizeOfNode(int node) {
    	return cSizes[labels[node]];
    }
    
    /**
     * Check if a node is an AP
     * 
     * @return true if the node is a AP and false otherwise
     */
    public boolean isAP(int node) {
    	return APs[node];
    }
    
    /**
     * Get all APs
     * 
     * @return list of APS
     */
    public ArrayList<Integer> getAPs() {
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	for (int u = 0; u < labels.length; u++)
    		if (APs[u]) list.add(u);
    	
    	return list;
    }
    
    /**
     * Update APs of a cluster
     */
    public void updateAPs(int cluster) {
    	problem.graph.computeAPs(labels, cluster, APs);
    }
    
    /**
     * Get the solution total cost. Note the the total cost may be out-dated if the
     * solution was modified. To ensure that it is updated, call {@link #updateCost()}
     *
     * @return the solution cost
     */
    public double getCost() {
        return totPenalty;
    }
    
    /**
     * Set the solution total cost
     * 
     * @param totPenalty	new total penalty	
     */
    public void setCost(double totPenalty) {
    	this.totPenalty = totPenalty;
    }
    
    /**
     * Update and return the cost of the solution
     *
     * @return the updated solution cost
     */
    public double updateCost() {
    	totPenalty = problem.calculateTotalPenalty(labels);
    	return totPenalty;
    }
    
    /**
     * Update and return the cost of the solution
     * @param delta		the difference	
     *
     * @return the updated solution cost
     */
    public void updateCost(double delta) {
    	totPenalty += delta;
    }
    
    /**
     * Validate the solution
     *
     * @param output  the output stream to print eventual error messages
     * @return true if the solution is valid and false otherwise
     */
    public boolean validate(PrintStream output) {
    	boolean valid = true;
    	
    	for (int c = 0; c < nClusters; c++) {
    		int[] cLabels = new int[labels.length];
    		int nComs = problem.graph.getComponents(labels, c, cLabels);
    		if (nComs == 0) {
    			output.printf("Cluster %d is empty\n", c);
    			valid = false;
    		}
    		else if (nComs > 1) {
    			output.printf("Cluster %d is disconnected\n", c);
    			valid = false;
    		}
    	}
    	
        return valid;
    }
    
    /**
     * Check if the node is valid to move
     * 
     * @param node	the node to move
     * @return true if the node is valid to move and false otherwise
     */
    public boolean isValidNode(int node) {
    	boolean valid = !APs[node] && 
    			cSizes[labels[node]] > 1;
    	
    	return valid;
    }
    
    /**
     * Check if the node is valid to move to a cluster
     * 
     * @param node	the node to move
     * @return true if the node is valid to move and false otherwise
     */
    public boolean isValidMove(int node, int target) {
    	boolean valid = !APs[node] && 
    			cSizes[labels[node]] > 1 &&
    			labels[node] != target && 
    			problem.graph.isNodeConnectedCluster(labels, node, target);
    	
    	return valid;
    }
    
    /**
     * Compute the gain when move a node to new cluster 
     *
     * @param node		the node to move
     * @param target	the target cluster
     * @return the move gain
     */
	public double getMoveGain(int node, int target) {
		return problem.computeMoveGain(labels, node, target);
	}

	/**
     * Compute the size of the clusters 
    */
    private static int[] computeClusterSizes(int nClusters, int[] labels) {
    	int[] cSizes = new int[nClusters];
    	Arrays.fill(cSizes, 0);
    	for (int u = 0; u < labels.length; u++)
    		cSizes[labels[u]] += 1;
    	
    	return cSizes;
	}

    /**
     * Compute the APs 
     */
    private static boolean[] computeAPs(Graph graph, int nClusters, int[] labels) {
    	boolean[] APs = new boolean[labels.length];
    	for (int c = 0; c < nClusters; c++)
    		graph.computeAPs(labels, c, APs);
    	
    	return APs;
	}
	
    /**
     * Normalize the labels of nodes 
     */
	public static int normalize(int[] labels) {
		int[] temp = labels.clone();
		int[] map = new int[temp.length];
		Arrays.fill(map, -1);
		int nClusters = 0;
		for (int u = 0; u < temp.length; u++)
			if (map[temp[u]] == -1) {
				labels[u] = nClusters++;
				map[temp[u]] = labels[u]; 
			} else
				labels[u] = map[temp[u]];
		return nClusters;
	}
	
	/**
     * Write the solution to a file
     *
     * @param filePath	the path of the output file
     * @throws IOException in case any IO error occurs
     */
    public void write(String filePath) throws IOException {
        BufferedWriter writer = Util.getFileWriter(filePath);

        writer.write(String.format("Total penalty: %.12f\n", totPenalty));
        writer.write("Solution: ");
        for (int l : labels) writer.write(l + " "); 
        
        writer.close();
    }

    /**
     * Write the solution to a PrintStream
     *
     * @param output	a PrintStream
     */
	public void write(PrintStream output) {
		output.printf("Solution:\n");
        for (int c = 0; c < nClusters; c++) {
        	output.printf("[");
        	for (int u = 0; u < labels.length; u++) {
                if (labels[u] == c) output.printf("%d, ", u);
            }
        	output.printf("]\n");
        }
	}

}