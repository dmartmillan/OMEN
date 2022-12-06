package cgcp.model;

/**
 * This class represents move updates of a solution.
 *
 * @author Hoang Tran
 */
public class MoveQueue {
	
	/***
     * The solution considered
     */
	public final Solution solution;

	/***
     * Node-cluster links
     * nodeLinks[node][cluster]
     */
    public int[][] nodeLinks;
    
    /***
     * Cluster-cluster links
     * clusterLinks[cluster][cluster]
     */
    public int[][] clusterLinks;
    
    /***
     * The cannot-link gains of moves
     * clGains[node][cluster]
     */
    public double[][] clGains;


    /***
     * The must-link gains of moves
     * mlGains[node][cluster]
     */
    public double[][] mlGains;
    
     /**
     * Instantiate a new MoveUpdate
     *
     * @param solution	the solution considered
     */
    public MoveQueue(Solution solution) {
        this.solution = solution;
    }
    
    /**
     * Update all node-cluster links and cluster-cluster links
     */
    public void updateMoves() {
    	Graph graph = solution.problem.graph;
    	
    	nodeLinks = new int[solution.length()][solution.nClusters()];
    	clusterLinks = new int[solution.nClusters()][solution.nClusters()];
    	for (int u = 0; u < solution.length(); u++) {
    		for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
    			int v = graph.adjList[p];
    			nodeLinks[u][solution.getLabel(v)] += 1;
    			clusterLinks[solution.getLabel(u)][solution.getLabel(v)] += 1;
    		}
    	}
    }
    
    /**
     * Update node-cluster links and cluster-cluster links after a move
     * 
     * @param node		the node moved
     * @param source	the source cluster
     * @param target	the target cluster
     */
    public void updateMoves(int node, int source, int target) {
    	Graph graph = solution.problem.graph;
    	
    	for (int p = graph.pointers[node]; p < graph.pointers[node + 1]; p++) {
			int u = graph.adjList[p];
			int c = solution.getLabel(u);
			nodeLinks[u][source] -= 1;
			nodeLinks[u][target] += 1;
			clusterLinks[c][source] -= 1;
			clusterLinks[source][c] -= 1;
			clusterLinks[c][target] += 1;
			clusterLinks[target][c] += 1;
    	}
    }
        
    /**
     * Update the link costs of all nodes
     */
    public void updateLinkCosts() {
    	CLConstraint cl = solution.problem.clCon;
    	MLConstraint ml = solution.problem.mlCon;
    	
    	clGains = new double[solution.length()][solution.nClusters()];
    	mlGains = new double[solution.length()][solution.nClusters()];
        for (int u = 0; u < solution.length(); u++)
        	for (int c = 0; c < solution.nClusters(); c++) {
    			for (int p = cl.pointers[u]; p < cl.pointers[u + 1]; p++) {
        			int v = cl.adjList[p];
        			if (solution.getLabel(v) == c)
        				clGains[u][c] += cl.penList[p];
    			}
    			for (int p = ml.pointers[u]; p < ml.pointers[u + 1]; p++) {
        			int v = ml.adjList[p];
        			if (solution.getLabel(v) != c)
        				mlGains[u][c] += ml.penList[p];
    			}
        	}
    }
    
    /**
     * Update the link costs of adjacent nodes of a node
     * 
     * @param node  the node
     * @param source  the source cluster of the node
     * @param target  the target to move 
     */
    public void updateLinkCosts(int node, int source, int target) {
    	CLConstraint cl = solution.problem.clCon;
    	MLConstraint ml = solution.problem.mlCon;
    	
    	for (int p = cl.pointers[node]; p < cl.pointers[node + 1]; p++) {
			int u = cl.adjList[p];
			double pen = cl.penList[p];
			clGains[u][source] -= pen;
    		clGains[u][target] += pen;
    	}
    	
    	for (int p = ml.pointers[node]; p < ml.pointers[node + 1]; p++) {
			int u = ml.adjList[p];
			double pen = ml.penList[p];
			mlGains[u][source] += pen;
    		mlGains[u][target] -= pen;
    	}    	
    }

    /**
     * Compute the gain when move a node to new cluster 
     *
     * @param node		the node to move
     * @param target	the target cluster
     * @return the move gain
     */
	public double getMoveGain(int node, int target) {
		Problem problem = solution.problem;
		int source = solution.getLabel(node);
		double cl = clGains[node][target] - clGains[node][source];
		double ml = mlGains[node][target] - mlGains[node][source];
		return problem.calculateGain(cl, ml);
	}

	/**
     * Check if the node is valid to move to a cluster
     * 
     * @param node	the node to move
     * @return true if the node is valid to move and false otherwise
     */
	public boolean isValidMove(int node, int target) {
		if (solution.getLabel(node) == target) return false;
		
		return nodeLinks[node][target] > 0;
	}
	
	/**
     * Check if two clusters are linked
     * 
     * @param cluster1	the cluster 1
     * @param cluster2	the cluster 2
     * @return true if the two clusters are linked and false otherwise
     */
	public boolean isLinkedClusters(int cluster1, int cluster2) {
		return clusterLinks[cluster1][cluster2] > 0;
	}

}
