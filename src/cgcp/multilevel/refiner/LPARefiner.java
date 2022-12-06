package cgcp.multilevel.refiner;

import java.util.ArrayList;
import java.util.Random;

import cgcp.model.Graph;
import cgcp.model.Solution;

/**
 * This class is a LPA Refining method implementation.
 *
 * @author Hoang Tran, Mohit Mohta
 */
public class LPARefiner extends Refiner {

	/**
     * Instantiate a new LPARefiner
     *
     * @param random  the random number generator
     */
	public LPARefiner(Random random) {
		super(random);
		
	}

	/**
     * Refine the clustering solution
     *
     * @param solution  the solution considered
     * @return the refined solution
     */
	@Override
	public Solution refine(Solution solution) {
		boolean stop = false;
		while (!stop) {
			stop = true;
			ArrayList<Integer> nodeList = new ArrayList<Integer>();
			for (int u = 0; u < solution.length(); u++) nodeList.add(u);
			
			while (nodeList.size() > 0) {
				int node = nodeList.remove(random.nextInt(nodeList.size()));
				if (solution.isValidNode(node))
					if(doMove(solution, node)) stop = false;
			}
		}
        
        return solution;
	}
	
	/**
	 * Perform the move and return true if the move is accepted
	 *
     * @param solution	the solution to be modified
     * @param node		the node to move
     * @return true if the move is accepted
	 */
	public boolean doMove(Solution solution, int node) {
		Graph graph = solution.problem.graph;
		
		int target = -1;
		double gain = Double.MAX_VALUE;
		boolean[] clusters = new boolean[solution.nClusters()];
		for (int p = graph.pointers[node]; p < graph.pointers[node + 1]; p++) {
			int adjNode = graph.adjList[p];
			if (solution.getLabel(node) != solution.getLabel(adjNode) &&
					!clusters[solution.getLabel(adjNode)]) {
				double g = solution.getMoveGain(node, solution.getLabel(adjNode));
				if (g < gain) {
					target = solution.getLabel(adjNode);
					gain = g;
				}
				clusters[target] = true;
			}
		}
		
		if (node != -1 && gain < -1e-12) {		
			// do the move
			int source = solution.getLabel(node);
			solution.setLabel(node, target);
			solution.updateCost(gain);
			solution.updateAPs(source);
			solution.updateAPs(target);
		
			return true;
		}
		
		return false;
	}
	
	/**
     * Return the string representation of LPARefiner
     *
     * @return the string representation of LPARefiner
     */
	public String toString() {
        return "LPA refiner";
    }

}