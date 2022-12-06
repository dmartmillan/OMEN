package cgcp.multilevel.refiner;

import java.util.Random;

import cgcp.model.Solution;

/**
 * This abstract class represents a Refining method.
 *
 * @author Hoang Tran, Mohit Mohta
 */
public abstract class Refiner {
	
	/***
     * Random number generator
     */
	protected Random random;
	
	/**
     * Instantiate a new Refiner
     *
     * @param random  the random number generator
     */
	public Refiner(Random random) {
		this.random = random;
	}
	
	/**
     * Refine the clustering solution
     *
     * @param solution  the solution considered
     * @return the refined solution
     */
	public abstract Solution refine(Solution solution);

}
