package cgcp.multilevel.refiner;

import java.util.Random;

import cgcp.model.Solution;

/**
 * This class is a KL Refining method implementation.
 *
 * @author Hoang Tran, Mohit Mohta
 */
public class KLRefiner extends Refiner {

	/**
     * Instantiate a new KLRefiner
     *
     * @param random  the random number generator
     */
	public KLRefiner(Random random) {
		super(random);
		
	}

	/**
     * Refine the clustering solution
     *
     * @param solution	the solution considered
     * @return the refined solution
     */
	@Override
	public Solution refine(Solution solution) {
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
    	
        int nPairMoveNotImp = 0;
        while (nPairMoveNotImp++ < nPairs) {
        	int pairTurn = random.nextInt(nPairs);
        	int cluster1 = pairs[pairTurn][0];
        	int cluster2 = pairs[pairTurn][1];
    		int turn = random.nextInt(2);
    		int nNodeMoveNotImp = 0;
    		while (nNodeMoveNotImp++ < 2) {
    			boolean accepted;
    			if (turn == 0) {
    				accepted = doMove(solution, cluster1, cluster2);
    			} else {
    				accepted = doMove(solution, cluster2, cluster1);
    			}
    			
                // if solution is improved
                if (accepted) {
                    nNodeMoveNotImp = 0;
                    nPairMoveNotImp = 0;
                }
                
    			turn = (turn + 1) % 2;
    		}
    	}
        
        return solution;
	}
	
	/**
	 * Perform the move and return true if the move is accepted
	 *
     * @param solution	the solution to be modified
     * @param source	the source cluster of moved node
     * @param target	the target cluster to move
     * @return true if the move is accepted
	 */
	public boolean doMove(Solution solution, int source, int target) {
		int node = -1;
		double gain = Double.MAX_VALUE;
		if (solution.getClusterSize(source) > 1)
			for (int u = 0; u < solution.length(); u++)
				if (solution.getLabel(u) == source && 
						solution.isValidMove(u, target)) {
					double g = solution.getMoveGain(u, target);
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
		
			return true;
		}
		
		return false;
	}

	/**
     * Return the string representation of KLRefiner
     *
     * @return the string representation of KLRefiner
     */
	public String toString() {
        return "KL refiner";
    }
	
}