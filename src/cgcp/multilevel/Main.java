package cgcp.multilevel;


import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import cgcp.model.CLConstraint;
import cgcp.model.Graph;
import cgcp.model.MLConstraint;
import cgcp.model.Problem;
import cgcp.model.Solution;
import cgcp.multilevel.clusterer.Clusterer;
import cgcp.multilevel.clusterer.KLILSClusterer;
import cgcp.multilevel.coarsener.AMGCoarsener;
import cgcp.multilevel.coarsener.Coarsener;
import cgcp.multilevel.coarsener.HEMCoarsener;
import cgcp.multilevel.method.MultiLevel;
import cgcp.multilevel.refiner.KLRefiner;
import cgcp.multilevel.refiner.Refiner;

/**
 *
 * @author Hoang Tran, Mohit Mohta
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
    public static String coarsen = "amg";
    
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
        String outPath = "C:/Users/woutv/eclipse-workspace/dp5/output";
        long seed = System.currentTimeMillis();
        String coarsen = "amg";
        Graph graph = new Graph(grPath);
        CLConstraint clCon = new CLConstraint(clPath);
        MLConstraint mlCon = new MLConstraint(mlPath);
        Problem problem = new Problem(gamma, graph, clCon, mlCon);
        Random random = new Random(seed);
        
        // create the coarsener
        Coarsener coarsener = null;
        switch (coarsen) {
        	case "hem":
        		coarsener = new HEMCoarsener(random);
        		break;
        	case "amg":
        		coarsener = new AMGCoarsener(random);
        		break;	
        	
        	default:
                printUsage();
                System.exit(-1);
        }
        
        // create the clusterer
        Clusterer clusterer = new KLILSClusterer(nClusters, random);
        		        
        // create the refiner
        Refiner refiner = new KLRefiner(random);
        
        // create the solver
        MultiLevel solver = new  MultiLevel(coarsener, clusterer, refiner);
        
        
        // perform the clustering
        long startTimeMillis = System.currentTimeMillis();
        System.out.printf("Instance....: size=%d, k=%d, gamma=%.1f\n", problem.graph.nNodes, nClusters, gamma);
        System.out.printf("Algorithm...: %s (%s, %s, %s)\n", solver, coarsener, clusterer, refiner);
        System.out.printf("Other params: seed=%d\n\n", seed);
        
        Solution solution = solver.cluster(nClusters, problem);
        assert solution.validate(System.err);
        solution.updateCost();
        
        System.out.printf("\n");
        System.out.printf("Best-value........: %.12f\n", solution.getCost());
        System.out.printf("Total runtime.....: %.2fs\n", (System.currentTimeMillis() - startTimeMillis) / 1000.0);
        
        solution.write(outPath);
    }

	/**
     * Print the program usage
     */
    public static void printUsage() {
        System.out.println("Usage: java -jar cgcp.jar <nCluster> <gamma> <graph> <cannotLink> <mustLink> <output> [options]");
        System.out.println("    <nCluster>   : Number of clusters k.");
        System.out.println("    <gamma>      : Trade-off between can-not-link penalty and must-link penalty.");
        System.out.println("    <graph>      : Path of the interaction graph file.");
        System.out.println("    <cannotLink> : Path of the cannot-link graph file.");
        System.out.println("    <mustLink>   : Path of the must-link graph file.");
        System.out.println("    <output>     : Path of the output clusters file.");
        System.out.println();
        System.out.println("Options:");
        System.out.println("    -crs <coarsener> : Heavy-edge matching (hem), Algebraic multigrid (amg) (default: " + coarsen + ").");
        System.out.println("    -seed <seed>     : random seed (default: system clock).");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("    java -jar cgcp.jar 32 0.5 graph.txt cannot_links.txt must_links.txt output.txt");
        System.out.println("    java -jar cgcp.jar 32 0.5 graph.txt cannot_links.txt must_links.txt output.txt -crs hem");
        System.out.println();
    }

    /**
     * Read the input arguments
     *
     * @param args  the input arguments
     */
    public static void readArgs(String args[]) {
        if (args.length < 6) {
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
                case "-crs":
                    coarsen = args[++index].toLowerCase();
                    break;
                case "-seed":
                    seed = Long.parseLong(args[++index]);
                    break;
                
                default:
                    printUsage();
                    System.exit(-1);
            }
        }
    }
    
}