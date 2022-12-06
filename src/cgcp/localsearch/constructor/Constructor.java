package cgcp.localsearch.constructor;

import java.util.Random;

import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 * This abstract class represents a Constructor that generate initial solution.
 *
 * @author Hoang Tran
 */
public abstract class Constructor {

	/***
     * Problem considered
     */
    public final Problem problem;
    
    /***
     * Random number generator
     */
    public final Random random;
    
    /***
     * Name of constructor
     */
    public final String name;

    /**
     * Instantiate a new Constructor
     *
     * @param problem	the problem
     * @param random	the random number generator
     * @param name		the name of constructor
     */
    public Constructor(Problem problem, Random random, String name) {
        this.problem = problem;
        this.random = random;
        this.name = name;
    }

    /**
     * Generate a initial solution
     *
     * @param nClusters		the number of clusters
     * @return the solution generated
     */
    public abstract Solution generate(int nClusters);

    /**
     * Returns the string representation of the constructor
     *
     * @return the string representation of the constructor
     */
    public String toString() {
        return name;
    }

}