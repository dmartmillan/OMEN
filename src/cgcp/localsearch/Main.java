package cgcp.localsearch;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import cgcp.localsearch.constructor.Constructor;
import cgcp.localsearch.constructor.KMeanClustering;
import cgcp.localsearch.heuristic.Heuristic;
import cgcp.localsearch.heuristic.KLILS;
import cgcp.model.CLConstraint;
import cgcp.model.Graph;
import cgcp.model.MLConstraint;
import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 *
 * @author Hoang Tran
 */
public class Main {

	/***
     * Application input arguments
     */
	public static int nClusters = 10;
	public static double gamma = 0.5;
    public static String grPath = "C:/Users/woutv/eclipse-workspace/dp5/graph.adjacency";
    public static String clPath = "C:/Users/woutv/eclipse-workspace/dp5/cannot.links";
    public static String mlPath = "C:/Users/woutv/eclipse-workspace/dp5/must.links";
    public static String outPath = "C:/Users/woutv/eclipse-workspace/dp5/output";

    /***
     * General input parameters
     */
    public static long seed = System.currentTimeMillis();
    public static long timeLimit = 600 * 1000;
    public static long maxIter = 50000;
    
    /***
     * KL-ILS input parameters
     */
    public static double wP = 0.2293;
    public static double iterP = 310;
    public static double p0Ratio = 0.1055;
    public static int pMax = 12;
    public static boolean acceptAP = true;
    
    /**
     * The entry point of application
     *
     * @param args  the input arguments
     * @throws IOException if any IO error occurs
     */
    public static void main(String[] args) throws IOException {
    	Locale.setDefault(new Locale("en-US"));        	
    	//readArgs(args);
    	int nClusters = 10;
        double gamma = 0.5;
        String grPath = "C:/Users/woutv/eclipse-workspace/dp5/graph.adjacency";
        String clPath = "C:/Users/woutv/eclipse-workspace/dp5/cannot.links";
        String mlPath = "C:/Users/woutv/eclipse-workspace/dp5/must.links";
        String outPath = "C:/Users/woutv/eclipse-workspace/dp5/output2";
    	Graph graph = new Graph(grPath);
    	CLConstraint clCon = new CLConstraint(clPath);
    	MLConstraint mlCon = new MLConstraint(mlPath);
        Problem problem = new Problem(gamma, graph, clCon, mlCon);
        Random random = new Random(seed);
        
        // create clustering solver
        Heuristic solver = new KLILS(problem, random, wP, iterP, p0Ratio, pMax, acceptAP);
        
        long startTimeMillis = System.currentTimeMillis();
        System.out.printf("Instance....: size=%d, k=%d, gamma=%.1f\n", problem.graph.nNodes, nClusters, gamma);
        System.out.printf("Algorithm...: %s\n", solver);
        System.out.printf("Other params: seed=%d, timeLimit=%.2fs\n\n", seed, timeLimit / 1000.0);
        
        // generate initial solution
        Constructor constructor = new KMeanClustering(problem, random);
        Solution solution = constructor.generate(nClusters);
        assert solution.validate(System.err);
        
        // perform the clustering 
        solution = solver.run(solution, timeLimit, maxIter, System.out);
        assert solution.validate(System.err);
        solution.updateCost();
	    
        // print result
        System.out.printf("\n");
        System.out.printf("Best-value........: %.12f\n", solution.getCost());
        System.out.printf("N. of Iterations..: %d\n", solver.getNIters());
        System.out.printf("Total runtime.....: %.2fs\n", (System.currentTimeMillis() - startTimeMillis) / 1000.0);
        
        solution.write(outPath);
    }

	/**
     * Print the program usage
     */
    public static void printUsage() {
        System.out.println("Usage: java -jar cgcp.jar <nCluster> <gamma> <graph> <cannotLink> <mustLink> <output> [options]");
        System.out.println("    <nClusters>   : Number of clusters k.");
        System.out.println("    <gamma>      : Trade-off between cannot-link penalty and must-link penalty.");
        System.out.println("    <graph>      : Path of the interaction graph file.");
        System.out.println("    <cannotLink> : Path of the cannot-link graph file.");
        System.out.println("    <mustLink>   : Path of the must-link graph file.");
        System.out.println("    <output>     : Path of the output clusters file.");
        System.out.println();
        System.out.println("Options:");
        System.out.println("    -seed <seed>         : random seed (default: system clock).");
        System.out.println("    -time <timeLimit>    : time limit in seconds (default: " + timeLimit / 1000 + ").");
        System.out.println("    -maxIters <maxIters> : maximum number of consecutive rejections (default: " + maxIter +").");
        System.out.println("    ILS parameters:");
        System.out.println("        -wP <wP>         : articulation point move probability = wp / |V| (default: " + wP + ").");
        System.out.println("        -itersP <itersP> : non-improvement iterations before perturbation = itersP (default: " + iterP + ").");
        System.out.println("        -p0 <p0>         : initial perturbation size = p0 * |V| (default: " + p0Ratio + ").");
        System.out.println("        -pMax <pMax>     : maximum perturbation level (default: " + pMax + ").");
        System.out.println("        -apMove <apMove> : 1 accept AP move, 0 not accept AP move (default: " + (acceptAP ? "1" : "0") + ").");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("    java -jar cgcp.jar 2 0.5 graph.txt cannot_links.txt must_links.txt output.txt -time 180");
        System.out.println("    java -jar cgcp.jar 2 0.5 graph.txt cannot_links.txt must_links.txt output.txt -time 180 -wP 0.01 -itersP 10.0 -p0 0.04 -pMax 5");
        System.out.println();
    }

    /**
     * Read the input arguments
     *
     * @param args  the input arguments
     */
    public static void readArgs(String args[]) {
        if (args.length < 6) {
        	System.out.printf("The number of parameter is not correct\n");
            printUsage();
            System.exit(-1);
        }

        int index = -1;

        nClusters = Integer.parseInt(args[++index]);
        gamma = Double.parseDouble(args[++index]);
        grPath = args[++index];
        clPath = args[++index];
        mlPath = args[++index];
        outPath = args[++index];

        while (index < args.length - 1) {
            String option = args[++index].toLowerCase();

            switch (option) {
                /* general parameters */
	            case "-seed":
                    seed = Long.parseLong(args[++index]);
                    break;
                case "-time":
                	timeLimit = Math.round(Double.parseDouble(args[++index]) * 1000.0);
                    break;
                case "-maxiters":
                    maxIter = Long.parseLong(args[++index]);
                    break;
                
                /* KL-ILS parameters */
                case "-wp":
                    wP = Double.parseDouble(args[++index]);
                    break;
                case "-itersp":
                    iterP = Double.parseDouble(args[++index]);
                    break;
                case "-p0":
                    p0Ratio = Double.parseDouble(args[++index]);
                    break;
                case "-pmax":
                    pMax = Integer.parseInt(args[++index]);
                	break;
                case "-apmove":
                    acceptAP = Integer.parseInt(args[++index]) == 1 ? true : false;
                    break;
                    
                default:
                	System.out.printf("The parameter %s is not found\n", option);
                    printUsage();
                    System.exit(-1);
            }
        }
    }
    
}