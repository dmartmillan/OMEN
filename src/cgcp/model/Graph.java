package cgcp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import cgcp.util.Util;

/**
 * This class represents an unweighted graph.
 *
 * @author Hoang Tran
 */
public class Graph {
	
	/***
     * The number of nodes
     */
    public final int nNodes;

    /***
     * The number of edges in the graph
     */
    public final int nEdges;
    
    /***
     * Pointers to adjacency list
     * pointers[pointer]
     */
    public final int[] pointers;
    
	/***
     * Adjacency list
     * adjList[node]
     */
    public final int[] adjList;
    
    /**
     * Instantiate a new Graph
     *
     * @param nNodes	the number of nodes
     * @param nEdges	the number of edges
     * @param pointers	the pointers to adjacency list
     * @param adjList	the adjacency list
     */
    public Graph(int nNodes, int nEdges, int[] pointers, int[] adjList) { 
    	this.nNodes = nNodes;
    	this.nEdges = nEdges;
    	this.pointers = pointers;
    	this.adjList = adjList; 	
    }
    
    /**
     * Instantiate a new Graph
     *
     * @param path	the file path
     * @throws IOException in case any IO error occurs
     */
    public Graph(String path) throws IOException {
    	BufferedReader reader = Util.getFileReader(path);
    	String line = null;
    	String[] strArray = null;
    	line = reader.readLine();
    	strArray = line.split(" ");
    	this.nNodes = Integer.parseInt(strArray[0]);
    	this.nEdges = Integer.parseInt(strArray[1]);
    	this.pointers = new int[nNodes + 1];
    	this.adjList = new int[nEdges * 2];
    	int pointer = 0;
    	for (int u = 0; u < nNodes; u++) {
    		pointers[u] = pointer;
    		line = reader.readLine();
        	strArray = line.split(" ");
        	ArrayList<Integer> list = new ArrayList<Integer>();
        	for (String str : strArray) {
        		if (str != "") {
        			int v = Integer.parseInt(str);
        			if (!list.contains(v)) {
        				adjList[pointer] = v;
        				pointer++;
        				list.add(v);
        			}
        		}
        	}
    	}
    	pointers[nNodes] = pointer;
    	reader.close();
    	if (pointer / 2 != nEdges) {
    		System.err.printf("The actual number of edges %d is different from "
    				+ "the expected number of edges %d", pointer / 2, nEdges);
    		System.exit(1);
    	}
    }

    /**
     * Validate the graph
     *
     * @param output	the output stream to print eventual error messages
     * @return true if the graph is connected and false otherwise
     */
	public boolean validate(PrintStream output) {
		int[] comLabels = new int[nNodes];
		Arrays.fill(comLabels, -1);
		boolean[] visited = new boolean[nNodes];
    	int nComponents = 0;
    	for (int u = 0; u < nNodes; u++) {
			if (!visited[u]) {
				comLabels[u] = nComponents++;
				ArrayList<Integer> queue = new ArrayList<Integer>();
				queue.add(u);
				visited[u] = true;
				while(!queue.isEmpty()) {
					int node = queue.remove(0);
					for (int p = pointers[node]; p < pointers[node + 1]; p++) {
						int v = adjList[p];
						if (!visited[v]) {
							comLabels[v] = nComponents - 1;
							queue.add(v);
							visited[v] = true;
						}
					}
				}
			}
    	}
    	
    	boolean valid = true;
    	if (nComponents > 1) {
    		valid = false;
    		output.printf("Graph is not connected. There are %d components\n", nComponents);
    	}
    	
    	return valid;
	}
	
	/**
     * Produce and return list of disconnected components in a cluster
     *
     * @param labels		the labels of graph's nodes
     * @param cluster		the cluster
     * @param comLabels		the labels of cluster's nodes
     * @return the number of disconnected components in the cluster
     */
	public int getComponents(int[] labels, int cluster, int[] comLabels) {
		Arrays.fill(comLabels, -1);
		boolean[] visited = new boolean[nNodes];
    	int nComponents = 0;
    	for (int u = 0; u < nNodes; u++) {
			if (labels[u] != cluster || visited[u]) continue;
			comLabels[u] = nComponents++;
			
			// start BFS from u
			ArrayList<Integer> queue = new ArrayList<Integer>();
			queue.add(u);
			visited[u] = true;
			while(!queue.isEmpty()) {
				int node = queue.remove(0);
				for (int p = pointers[node]; p < pointers[node + 1]; p++) {
					int v = adjList[p];
					if (labels[v] == cluster && !visited[v]) {
						comLabels[v] = nComponents - 1;
						queue.add(v);
						visited[v] = true;
					}
				}
			}
    	}
    	
    	return nComponents;
	}
	
	/**
     * Find articulation points in a cluster
     *
     * @param labels	the labels of nodes
     * @param cluster	the cluster
     * @param ap		indicate if a node is a AP or not
     * @return the number of disconnected components in the cluster
     */
	private int time = 0;
	public void computeAPs(int[] labels, int cluster, boolean[] ap) { 
        // mark all the vertices as not visited 
        boolean visited[] = new boolean[nNodes];
        Arrays.fill(visited, false);
        int parent[] = new int[nNodes];
        Arrays.fill(parent, -1);
        int disc[] = new int[nNodes]; 
        int low[] = new int[nNodes]; 
        for (int i = 0; i < nNodes; i++)
        	if (labels[i] == cluster)
        		ap[i] = false;
        time = 0;
        		
        // call the recursive function to find articulation 
        for (int u = 0; u < nNodes; u++) {
        	if(labels[u] == cluster && visited[u] == false)
        		APUtil(labels,cluster, u, visited, disc, low, parent, ap);
        }
    }     

	private void APUtil(int[] labels, int cluster, int u, boolean visited[], int disc[], int low[], int parent[], boolean ap[]) { 

		int children = 0; 
		visited[u] = true; 
		disc[u] = low[u] = ++time; 
		
		for (int p = pointers[u]; p < pointers[u + 1]; p++) {
			int v = adjList[p];
			if (labels[v] == cluster)
				if (!visited[v]) { 
			        children++; 
			        parent[v] = u; 
			        APUtil(labels, cluster, v, visited, disc, low, parent, ap); 
			        low[u] = Math.min(low[u], low[v]); 
			        if (parent[u] == -1 && children > 1) 
			            ap[u] = true; 
			        if (parent[u] != -1 && low[v] >= disc[u]) 
			            ap[u] = true; 
			    } 
				else if (v != parent[u]) 
					low[u] = Math.min(low[u], disc[v]); 
		} 
	}
	
	/**
     * Produce coarse graph 
     *
     * @param nClusters		the number of clusters
     * @param labels		the labels of nodes
     */
	public Graph productCoarserGraph(int nClusters, int[] labels) {
		int nCrsNodes = nClusters;
		int nCrsEdges = 0;
		ArrayList<HashSet<Integer>> adjArrList = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < nCrsNodes; i++) adjArrList.add(new HashSet<Integer>());
		for (int u = 0; u < nNodes; u++) {
			for (int p = pointers[u]; p < pointers[u + 1]; p++) {
				int v = adjList[p];
				if (u < v) {
					int node1 = labels[u];
					int node2 = labels[v];
					if (node1 != node2 && !adjArrList.get(node1).contains(node2)) {
						adjArrList.get(node1).add(node2);
						adjArrList.get(node2).add(node1);
						nCrsEdges++;
					}
				}
			}
		}
		
		int[] crsPointers = new int[nCrsNodes + 1];
		int[] crsAdjList = new int[nCrsEdges * 2];
		int pointer = 0;
		for (int u = 0; u < nCrsNodes; u++) {
    		crsPointers[u] = pointer;
    		for (Integer v : adjArrList.get(u)) {
        		crsAdjList[pointer] = v;
        		pointer++;
        	}
    	}
    	crsPointers[nCrsNodes] = pointer;
    	if (pointer != nCrsEdges * 2) System.err.println("nEdges is wrong!");
    	Graph newGraph = new Graph(nCrsNodes, nCrsEdges, crsPointers, crsAdjList);
    	
    	return newGraph;
	}

	/**
     * Check if a node is connected to a cluster 
     *
     * @param labels	the labels of nodes
     * @param node		the node to check
     * @param target	the target cluster
     */
	public boolean isNodeConnectedCluster(int[] labels, int node, int target) {
		int p = pointers[node];
		while (p < pointers[node + 1] && labels[adjList[p]] != target) p++;
		boolean connected = true;
		if (p == pointers[node + 1]) connected = false;
		
		return connected;
	}

}