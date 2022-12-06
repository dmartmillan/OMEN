package cgcp.multilevel.clusterer;

import java.util.Random;

import cgcp.localsearch.constructor.Constructor;
import cgcp.localsearch.constructor.KMeanClustering;
import cgcp.localsearch.heuristic.KLILS;
import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 * This class is the KL-ILS clustering method implementation.
 *
 * @author Hoang Tran, Mohit Mohta
 */
public class KLILSClusterer extends Clusterer {
	
	private long timeLimit = 600; 
	private long maxIter = (long) 50000;
	
	private int nClusters;
	
	/**
     * Instantiate a new KLILSClusterer.
     *
     * @param random  the random number generator
     */
	public KLILSClusterer(int nClusters, Random random) {
		super(random);
		
		this.nClusters = nClusters;
	}

	@Override
	public Solution cluster(Problem problem) {
		Constructor constructor = new KMeanClustering(problem, random);        
        Solution solution = constructor.generate(nClusters);
        assert solution.validate(System.err);
        
        KLILS solver = new KLILS(problem, random);
        solution = solver.run(solution, timeLimit * problem.graph.nNodes, maxIter, System.out);
        assert solution.validate(System.err);
        
		return solution;
	}
	
	/**
     * Return the string representation of the method
     *
     * @return the string representation of the method
     */
	public String toString() {
        return "KL-ILS clusterer";
    }
	
}
