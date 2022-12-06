package cgcp.localsearch.heuristic;

import java.io.PrintStream;
import java.util.Random;

import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 * This abstract class represents a Heuristic.
 *
 * @author Hoang Tran
 */
public abstract class Heuristic {

	/***
     * Problem considered
     */
    public final Problem problem;
    
    /***
     * Random number generator
     */
    public final Random random;
    
    /***
     * Name of the method
     */
    public final String name;
    
    /***
     * Start time in millisecond
     */
    protected long startTimeMillis = 0;

    /***
     * End time in millisecond
     */
    protected long finalTimeMillis = 0;
    
    /***
     * The number of iterations
     */
    protected long nIters = 0;
    
    /***
     * Best solution obtained
     */
    protected Solution bSolution;
    
    /**
     * Instantiate a new Heuristic
     *
     * @param problem	the problem
     * @param random	the random number generator
     * @param name		the name of method
     */
    public Heuristic(Problem problem, Random random, String name) {
        this.problem = problem;
        this.random = random;
        this.name = name;
    }

    /**
     * Run the local search and return the best solution obtained
     *
     * @param solution		the initial solution
     * @param timeLimit		the time limit in milliseconds
     * @param maxIter		the maximum number of iterations without improvement
     * @param output		the output stream
     * @return the best solution obtained
     */
    public abstract Solution run(Solution solution, long timeLimit, long maxIter, PrintStream output);

    // region getters and setters

    /**
     * Get best solution
     *
     * @return the best solution obtained so far
     */
    public Solution getBestSolution() {
        return bSolution;
    }

    /**
     * Get the number of iterations executed
     *
     * @return the number of iterations
     */
    public long getNIters() {
        return nIters;
    }

    /**
     * Return the string representation of the heuristic
     *
     * @return the string representation of the heuristic
     */
    public String toString() {
        return name;
    }

    // endregion getters and setters
}
