package cgcp.localsearch.heuristic;

import java.io.PrintStream;
import java.util.Random;

import cgcp.model.MoveQueue;
import cgcp.model.Problem;
import cgcp.model.Solution;
import cgcp.util.Util;

/**
 * This class is the KL-ILS implementation.
 *
 * @author Hoang Tran
 */
public class KLILS extends Heuristic {

    /**
     * ILS parameters
     */
	private double wP = 0.2293;;
	private double iterP = 310;
	private double p0Ratio = 0.1055;
    private int pMax = 12;
    private boolean acceptAP = true;
    
    /**
     * Instantiate a new KL-ILS
     *
     * @param problem	the problem
     * @param random	the random number generator
     */
    public KLILS(Problem problem, Random random) {
        super(problem, random, "KL-ILS");
    }
    
    /**
     * Instantiate a new KL-ILS
     *
     * @param problem	the problem
     * @param random	the random number generator
     * @param acceptAP	indicate if AP move is accepted
     */
    public KLILS(Problem problem, Random random, boolean acceptAP) {
        super(problem, random, "KL-ILS");
        
        this.acceptAP = acceptAP;
    }
    
    /**
     * Instantiate a new KL-ILS
     *
     * @param problem	the problem
     * @param random	the random number generator
     * @param wP		the articulation point move probability
     * @param iterP		the non-improvement iterations before perturbation
     * @param p0Ratio	the initial perturbation size ratio
     * @param pMax		the maximum perturbation level
     * @param acceptAP	indicate if AP move is accepted
     */
    public KLILS(Problem problem, Random random, double wP, double iterP, double p0Ratio, int pMax, boolean acceptAP) {
        super(problem, random, "KL-ILS");

        this.wP = wP;
        this.iterP = iterP;
        this.p0Ratio = p0Ratio;
        this.pMax = pMax;
        this.acceptAP = acceptAP;
    }

    @Override
    public Solution run(Solution solution, long timeLimit, long maxIter, PrintStream output) {
    	startTimeMillis = System.currentTimeMillis();
    	finalTimeMillis = startTimeMillis + timeLimit;    	
    	nIters = 0;
    	Heuristic heuristic = new KL(problem, random);
    	int nNodes = solution.length();
    	wP = wP / solution.length();
      	int p0 = (int) Math.round(p0Ratio * nNodes);
    	int perturbLevel = 1;
        int nItersInPerturb = 0;
        bSolution = solution.clone();
        Util.safePrintHeader(output);
        Util.safePrintStatus(System.out, 0, System.currentTimeMillis(), solution.getCost(), "s0");
        while (System.currentTimeMillis() < finalTimeMillis && nIters < maxIter) {
        	// run the heuristic
        	solution = heuristic.run(solution, timeLimit, maxIter, output);
            
        	// check if the solution is improved
        	if (solution.getCost() - bSolution.getCost() < -1e-12) {
    			Util.safePrintStatus(output, nIters, startTimeMillis, solution.getCost(), "p-" + perturbLevel);
    			nItersInPerturb = 0;
    			perturbLevel = 1;
        		bSolution = solution;
            }
            else nItersInPerturb++;
        	
        	// apply the perturbation
        	solution = bSolution.clone();  
        	MoveQueue queue = new MoveQueue(solution);
        	queue.updateMoves();
        	int perturbSize = perturbLevel * p0;
        	int i = 0;
        	while (i < perturbSize)
        		if (doMove(solution, queue)) i++;
        	solution.updateCost();
        	
        	// update the perturbation level
        	if (nItersInPerturb >= iterP) {
            	nItersInPerturb = 0;
                perturbLevel = perturbLevel + 1 <= pMax ? perturbLevel + 1 : 1;
            }

        	nIters++;
        }
        Util.safePrintFooter(output);
        
        return bSolution;
    }

    /**
	 * Perform the move and return true if the move is accepted
	 *
     * @param solution	the solution to be modified
     * @param queue		the move queue of the solution
     * @return true if the move is accepted
	 */
    private boolean doMove(Solution solution, MoveQueue queue) {
    	// select a valid move at random
		int node = -1;
		int target = -1;
    	do {
			node = random.nextInt(solution.length());
			target = random.nextInt(solution.nClusters());
		} while (solution.getClusterSizeOfNode(node) < 2 || !queue.isValidMove(node, target));
    	
    	if (solution.isAP(node)) {
    		if (acceptAP && random.nextDouble() < wP) {
	    		// do the move
	    		solution.setAPLabel(node, target, random);
				solution.updateCost();
				
				// update queue
				queue.updateMoves();
				
				return true;
    		}
    	} else {
	    	// do the move
	    	int source = solution.getLabel(node);
	    	solution.setLabel(node, target);
	    	solution.updateAPs(source);
			solution.updateAPs(target);
			
			// update queue
			queue.updateMoves(node, source, target);
			
			return true;
    	}
    	
    	return false;
	}
    
    /**
     * Return the string representation of the heuristic
     *
     * @return the string representation of the heuristic (with parameters values)
     */
    public String toString() {
        return String.format("KL-ILS (wP=%f, iterP=%f, p0Ratio=%f, pMax=%d)", wP, iterP, p0Ratio, pMax);
    }

}