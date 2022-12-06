package cgcp.multilevel.coarsener;

import java.util.Random;

import cgcp.model.Problem;

/**
 * This abstract class represents a Coarsening method.
 *
 * @author Hoang Tran, Mohit Mohta.
 */
public abstract class Coarsener {

	/***
     * Random number generator
     */
	public final Random random;
	
	/**
     * Instantiate a new Coarsener
     *
     * @param random	the random number generator
     */
	public Coarsener(Random random) {
		this.random = random;
	}
	
	/**
     * Perform the coarsening and return the coarse graph
     *
     * @param problem	original problem
     * @return the coarsening solution
     */
	public abstract CoarseningInfo coarsen(Problem problem);
	
}