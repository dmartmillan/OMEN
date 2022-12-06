package cgcp.multilevel.clusterer;

import java.util.Random;

import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 * This abstract class represents a Clustering method.
 *
 * @author Hoang Tran, Mohit Mohta
 */
public abstract class Clusterer {
	
	/***
     * Random number generator
     */
	protected Random random;
	
	/**
     * Instantiate a new Clusterer
     *
     * @param random  the random number generator
     */
	public Clusterer(Random random) {
		this.random = random;
	}

    /**
     * Perform the clustering and return the best solution obtained
     *
     * @param problem  the problem considered
     * @return the best solution obtained
     */
	public abstract Solution cluster (Problem problem);

}