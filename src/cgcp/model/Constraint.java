package cgcp.model;

import java.io.BufferedReader;
import java.io.IOException;

import cgcp.util.Util;

/**
 * This class represents a set of pairwise constraints.
 *
 * @author Hoang Tran
 */
public abstract class Constraint {

    /***
     * The number of nodes
     */
	protected final int nNodes;

	/***
     * The number of the constraints.
     */
	protected final int nCons;
    
    /***
     * Pointers to adjacency list
     * pointers[node]
     */
    public final int[] pointers;
    
	/***
     * Adjacency list
     * adjList[pointer]
     */
    public final int[] adjList;

    /***
     * Penalty list
     * penList[pointer]
     */
    public final double[] penList;

    /***
     * The total inner penalty
     */
    public double totInPenalty;
    
    /***
     * The total outer penalty
     */
    public double totOutPenalty;
    
    /***
     * The total penalty
     */
    public double totPenalty;
    
    /***
     * The maximum penalty
     */
    public double maxPenalty;

    /**
     * Instantiate a new Constraints
     *
     * @param nNodes		the number of nodes
     * @param nCons			the number of constraints
     * @param pointers		the pointers to adjacency and penalty list
     * @param adjList		the adjacent list
     * @param penList		the penalty list
     * @param totInPenalty	the total inner penalty
     */
    public Constraint(int nNodes, int nCons, int[] pointers, int[] adjList, double[] penList, double totInPenalty) {
    	this.nNodes = nNodes;
    	this.nCons = nCons;
    	this.pointers = pointers;
    	this.adjList = adjList;
    	this.penList = penList;
    	this.maxPenalty = 0;
    	this.totInPenalty = totInPenalty;
    	this.totOutPenalty = 0;
    	for (int u = 0; u < nNodes; u++) {
    		for (int p = pointers[u]; p < pointers[u + 1]; p++) {
    			int v = adjList[p];
    			if (u < v) {
    				double penalty = penList[p];
    				if (penalty > maxPenalty) maxPenalty = penalty;
    				totOutPenalty += penalty;
    			}
    		}
    	}
    	this.totPenalty = this.totInPenalty + this.totOutPenalty;
    }

    /**
     * Instantiate a new Constraints
     *
     * @param path	the file path
     * @throws IOException in case any IO error occurs
     */
    public Constraint(String path) throws IOException {
    	BufferedReader reader = Util.getFileReader(path);
    	String line = null;
    	String[] strArray = null;
    	line = reader.readLine();
    	strArray = line.split(" ");
    	this.nNodes = Integer.parseInt(strArray[0]);
    	this.nCons = Integer.parseInt(strArray[1]);
    	this.pointers = new int[nNodes + 1];
    	this.adjList = new int[nCons * 2];
    	this.penList = new double[nCons * 2];
    	this.maxPenalty = 0;
    	this.totInPenalty = 0;
    	this.totOutPenalty = 0;
    	int pointer = 0;
    	for (int u = 0; u < nNodes; u++) {
    		pointers[u] = pointer;
    		line = reader.readLine();
        	strArray = line.split(" ");
        	for (int i = 0; i < strArray.length / 2; i++) {
        		int v = Integer.parseInt(strArray[i * 2]);
        		double penalty = Double.parseDouble(strArray[i * 2 + 1]);
        		adjList[pointer] = v;
        		penList[pointer] = penalty;
        		if (u < v) {
        			if (penalty > maxPenalty) maxPenalty = penalty;
        			totOutPenalty += penalty;
        		}
    			pointer++;
        	}
    	}
    	pointers[nNodes] = pointer;
    	reader.close();
    	if (pointer / 2 != nCons) {
    		System.err.printf("The actual number of constraints %d is different from "
    				+ "the expected number of constraints %d", pointer / 2, nCons);
    		System.exit(1);
    	}
    	this.totPenalty = this.totInPenalty + this.totOutPenalty;
    }
   
}