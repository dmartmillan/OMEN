package cgcp.model;


/**
 * This class represents an Constrained Graph Clustering Problem.
 *
 * @author Hoang Tran
 */
public class Problem {

    /***
     * Trade-off between two objective functions
     */
    public final double gamma;
    
    /***
     * Unweighted graph
     */
    public final Graph graph;

    /***
     * Cannot-link constraint
     */
    public final CLConstraint clCon;

    /***
     * Must-link constraint
     */
    public final MLConstraint mlCon;
    
    /**
     * Instantiate a new Problem
     *
     * @param gamma		the trade-off 
     * @param graph		the graph
     * @param clCon	the cannot-link constraint
     * @param mlCon	the must-link constraint
     */
    public Problem(double gamma, Graph graph, CLConstraint clCon, MLConstraint mlCon) {
    	this.gamma = gamma;
    	this.graph = graph;
    	this.clCon = clCon;
    	this.mlCon = mlCon;
    }
	
    /**
     * Calculate the gain 
     *
     * @param clGain	the cannot-link gain
     * @param mlGain	the must-link gain
     * @return the gain
     */
	public double calculateGain(double clGain, double mlGain) {
		double totGain =  gamma * (clGain / clCon.totPenalty) + 
    			(1 - gamma) * (mlGain / mlCon.totPenalty);
    	return totGain;
	}

	/**
     * Calculate the total penalty 
     *
     * @param lables	the labels of nodes
     * @return the total penalty
     */
	public double calculateTotalPenalty(int[] labels) {
		double clPen = clCon.computeViolationPenalty(labels);
    	double mlPen = mlCon.computeViolationPenalty(labels);
    	double totPen =  gamma * ((clPen + clCon.totInPenalty) / clCon.totPenalty) + 
    			(1 - gamma) * (mlPen / mlCon.totPenalty);
    	
    	return totPen;
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
		double clGain = clCon.computeAssignGain(labels, node, target);
		double mlGain = mlCon.computeAssignGain(labels, node, target);
		return calculateGain(clGain, mlGain);
	}
    
	/**
     * Compute the gain when remove a node from its cluster 
     *
     * @param labels	the labels of nodes
     * @param node		the node to remove
     * @return the remove gain
     */
	public double computeRemoveGain(int[] labels, int node) {
		double clGain = clCon.computeRemoveGain(labels, node);
		double mlGain = mlCon.computeRemoveGain(labels, node);
		return calculateGain(clGain, mlGain);
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
		double clGain = clCon.computeMoveGain(labels, node, target);
		double mlGain = mlCon.computeMoveGain(labels, node, target);
		return calculateGain(clGain, mlGain);
	}
	
	/**
     * Correct the clusters that are disconnected. The number of clusters is increased.
     *
     * @param nClusters		the number of clusters
     * @param labels	the labels of nodes
     * @return the new number of clusters
     */
	public int correctClusters(int nClusters, int[] labels) {
		int nNewClusters = nClusters;
		for (int c = 0; c < nClusters; c++) {
			int[] comLabels = new int[labels.length];
			int nComponents = graph.getComponents(labels, c, comLabels);
			if (nComponents > 1) {
				for (int u = 0; u < graph.nNodes; u++)
					if (comLabels[u] > 0)
						labels[u] = nNewClusters + comLabels[u] - 1;
				nNewClusters += nComponents - 1;
			}
		}
		return nNewClusters;
	}

}