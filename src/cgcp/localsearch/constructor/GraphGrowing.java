package cgcp.localsearch.constructor;

import java.util.Arrays;
import java.util.Random;

import cgcp.model.Graph;
import cgcp.model.Problem;
import cgcp.model.Solution;

/**
 * This class contains a constructive procedure based on Graph Growing.
 *
 * @author Hoang Tran
 */
public class GraphGrowing extends Constructor {

	/**
     * Instantiate a new GraphGrowing
     *
     * @param proplem	the problem
     * @param random	the random number generator 
     */
	public GraphGrowing(Problem problem, Random random) {
		super(problem, random, "Graph Growing");
	}
	
	@Override
	public Solution generate(int nClusters) {
		Graph graph = problem.graph;
		int nNodes = graph.nNodes;
		int[] labels = new int[nNodes];
		Arrays.fill(labels, -1);

		// select cluster seeds at random
		for (int c = 0; c < nClusters; c++) {
			int u = -1;
			do u = random.nextInt(nNodes);
			while (labels[u] != -1);
			labels[u] = c;
		}

		// assign node to clusters alternately
		for (int i = 0; i < nNodes - nClusters; i++) {
			int node = -1;
			int target = -1;
			double minGain = Double.MAX_VALUE;
			for (int u = 0; u < nNodes; u++) {
				if (labels[u] != -1) continue;
				boolean[] clusters = new boolean[nClusters];
				Arrays.fill(clusters, false);
				for (int p = graph.pointers[u]; p < graph.pointers[u + 1]; p++) {
					int v = graph.adjList[p];
					if (labels[v] == -1 || clusters[labels[v]]) continue;
					double gain = problem.computeAssignGain(labels, u, labels[v]);
					if (gain < minGain) {
						node = u;
						target = labels[v];
						minGain = gain;
					}
					clusters[labels[v]] = true;
				}
			}
			labels[node] = target;
		}

		return new Solution(problem, nClusters, labels);
	}
}