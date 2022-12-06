package cgcp.localsearch.heuristic;

import java.io.PrintStream;
import java.util.Random;

import cgcp.model.MoveQueue;
import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 * This class is a KL algorithm implementation.
 *
 * @author Hoang Tran
 */
public class KL extends Heuristic {
    
	/**
     * Instantiate a new KL
     *
     * @param problem	the problem
     * @param random	the random number generator
     */
    public KL(Problem problem, Random random) {
        super(problem, random, "KL");
    }

    @Override
    public Solution run(Solution solution, long timeLimit, long maxIter, PrintStream output) {
    	nIters = 0;
    	int nClusters = solution.nClusters(); 
    	int nPairs = nClusters * (nClusters - 1);
        int[][] pairs = new int[nPairs][2];
        int id = -1;
        for (int c1 = 0; c1 < nClusters; c1++)
        	for (int c2 = 0; c2 < nClusters; c2++)
        		if( c1 != c2) {
        			id++;
        			pairs[id][0] = c1;
        			pairs[id][1] = c2;
        		}
    	
        MoveQueue queue = new MoveQueue(solution);
    	queue.updateMoves();
    	queue.updateLinkCosts();
    	int nPairMoveNotImp = 0;
        while (nPairMoveNotImp++ < nPairs) {
        	int pairTurn = random.nextInt(nPairs);
        	int cluster1 = pairs[pairTurn][0];
        	int cluster2 = pairs[pairTurn][1];
        	if (queue.isLinkedClusters(cluster1, cluster2)) {
	        	int turn = random.nextInt(2);
	    		int nNodeMoveNotImp = 0;
	    		while (nNodeMoveNotImp++ < 2) {
	    			boolean accepted;
	    			if (turn == 0) {
	    				accepted = doMove(solution, queue, cluster1, cluster2);
	    			} else {
	    				accepted = doMove(solution, queue, cluster2, cluster1);
	    			}
	    			
	                // if solution is improved
	                if (accepted) {
	                    nNodeMoveNotImp = 0;
	                    nPairMoveNotImp = 0;
	                }
	                
	    			turn = (turn + 1) % 2;
	    			nIters++;
	    		}
        	}
    	}
        
        return solution;
    }

    /**
	 * Perform the move and return true if the move is accepted
	 *
     * @param solution	the solution to be modified
     * @param queue		the move updates of the solution
     * @param source	the source cluster of moved node
     * @param target  	the target cluster to move
     * @return true if the move is accepted
	 */
    private boolean doMove(Solution solution, MoveQueue queue, int source, int target) {
    	int node = -1;
		double gain = Double.MAX_VALUE;
		
		if (solution.getClusterSize(source) < 2) return false; 	
		for (int u = 0; u < solution.length(); u++)
			if (solution.getLabel(u) == source)
				if (solution.isValidNode(u) && queue.isValidMove(u, target)) {
					double g = queue.getMoveGain(u, target);
					if (g < gain) {
						node = u;
						gain = g;
					}
				}
	
		if (node != -1 && gain < -1e-12) {
			// do the move
			solution.setLabel(node, target);
			solution.updateCost(gain);
			solution.updateAPs(source);
			solution.updateAPs(target);
	
	    	// update queue
			queue.updateMoves(node, source, target);
			queue.updateLinkCosts(node, source, target);
			
			return true;
		}
		
		return false;
	}
    
    /**
     * Return the string representation of this heuristic
     *
     * @return the string representation of this heuristic
     */
    public String toString() {
        return String.format("KL Algorithm");
    }

}