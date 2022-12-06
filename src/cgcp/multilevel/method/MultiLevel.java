package cgcp.multilevel.method;

import java.util.Stack;

import cgcp.model.CLConstraint;
import cgcp.model.Graph;
import cgcp.model.MLConstraint;
import cgcp.model.Problem;
import cgcp.model.Solution;
import cgcp.multilevel.clusterer.Clusterer;
import cgcp.multilevel.coarsener.Coarsener;
import cgcp.multilevel.coarsener.CoarseningInfo;
import cgcp.multilevel.refiner.Refiner;

/**
 * This class is the Multilevel k-way clustering implementation.
 *
 * @author Hoang Tran, Mohit Mohta
 */
public class MultiLevel {
	/***
     * Coarsening threshold
     */
	private int crsThres = 1000;
	
	/***
     * Coarsening method
     */
	private Coarsener coarsener;
	
	/***
     * Clustering method
     */
	private Clusterer clusterer;
	
	/***
     * Refining method
     */
	private Refiner refiner;
	
	/**
     * Instantiate a new MultilevelMethod
     *
     * @param coarsener	the coarsener
     * @param clusteror	the clusteror
     * @param refiner	the refiner
     */
	public MultiLevel(Coarsener coarsener, Clusterer clusteror, Refiner refiner) {
		this.coarsener = coarsener;
		this.clusterer = clusteror;
		this.refiner = refiner;
	}
	
	/**
     * Instantiate a new MultilevelMethod
     *
     * @param coarsener	the coarsener
     * @param clusterer	the clusterer
     * @param refiner	the refiner
     * @param crsThres	the coarsening threshold
     */
	public MultiLevel(Coarsener coarsener, Clusterer clusterer, Refiner refiner, int crsThres) {
		this.coarsener = coarsener;
		this.clusterer = clusterer;
		this.refiner = refiner;
		this.crsThres = crsThres;
	}
	
	/**
     * Perform the clustering
     *
     * @param problem  the problem
     * @return the best solution found by the method
     */
	public Solution cluster(int nClusters, Problem problem) {
		Stack<Problem> problems = new Stack<Problem>();
		Stack<CoarseningInfo> crsInfos = new Stack<CoarseningInfo>();
		
		// 1. Coarsening phase 
		System.out.printf("1. Coarsening phase:\n");
		int level = 0;
		problem.graph.validate(System.err);
		problems.push(problem); 
		System.out.printf("Level %d: graph size = %d\n", level++, problem.graph.nNodes);
		// coarsen the graph until its size less than coarsening threshold
		while (problem.graph.nNodes > crsThres) {
			// perform coarsening
			CoarseningInfo crsInfo = coarsener.coarsen(problem);
			Problem crsProblem = produceCoarserProblem(problem, crsInfo);
			problems.push(crsProblem);
			crsInfos.push(crsInfo);
			problem = crsProblem;
			System.out.printf("Level %d: graph size = %d\n", level++, problem.graph.nNodes);
		} 

		// 2. Initial partitioning phase
		System.out.printf("\n2. Initial partitioning phase (level %d)\n", --level);
		Problem initProblem = problems.pop();
		if (initProblem.graph.nNodes < nClusters) {
			System.err.print("Coarsenest graph\'s size is too small\n");
			System.exit(1);
		}
		Solution solution = clusterer.cluster(initProblem);
		
		// 3. Uncoarsening and refining phase
		System.out.printf("\n3. Uncoarsening and refining phase\n");
		while (!problems.empty()) {
			// produce the solution of upper level
			solution = produceFinerSolution(problems.pop(), crsInfos.pop(), solution);
			
			// perform refining
			System.out.println(String.format("Refining level %d (problem size: %d)", --level, solution.problem.graph.nNodes));
			System.out.println(String.format("  Before cost: %.12f", solution.getCost()));			
			solution = refiner.refine(solution);
			System.out.println(String.format("  After cost:  %.12f", solution.getCost()));
		}
				
		return solution;
	}

	/**
     * Produce and return the coarser problem of the problem considered
     *
     * @param problem	the problem considered
     * @param crsInfo	the coarsening solution
     * @return the coarser problem
     */
	public static Problem produceCoarserProblem(Problem problem, CoarseningInfo crsInfo) {
		Graph crsGraph = problem.graph.productCoarserGraph(crsInfo.nCrsNodes(), crsInfo.nodeMap());
    	CLConstraint crsCLCon = (CLConstraint) problem.clCon.produceCoarseConstraint(crsInfo.nCrsNodes(), crsInfo.nodeMap());
    	MLConstraint crsMLCon = (MLConstraint) problem.mlCon.productCoarserConstraint(crsInfo.nCrsNodes(), crsInfo.nodeMap());
    	Problem crsProblem = new Problem(problem.gamma, crsGraph, crsCLCon, crsMLCon);
		return crsProblem;
	}
	
	/**
     * Produce and return the upper solution of the solution considered
     *
     * @param problem	the upper problem
     * @param crsInfo	the coarsening solution
     * @param solution	the solution considered
     * @return the best solution obtained
     */
	private Solution produceFinerSolution(Problem finerProblem, CoarseningInfo crsInfo, Solution solution) {
		// produce the upper solution
		int[] finerLabels = new int[finerProblem.graph.nNodes];
		for (int u = 0; u < finerProblem.graph.nNodes; u++)
			finerLabels[u] = solution.getLabel(crsInfo.nodeMap(u));
		Solution upperSolution = new Solution(finerProblem, solution.nClusters(), finerLabels);
		
		return upperSolution;
	}
	
	/**
     * Return the string representation of the method
     *
     * @return the string representation of the method
     */
	public String toString() {
        return "Multilevel clustering";
    }

}