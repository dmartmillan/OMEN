package cgcp.multilevel.coarsener;

/**
 * This class represents a coarsening solution.
 *
 * @author Hoang Tran, Mohit Mohta
 */
public class CoarseningInfo {
	
	private int nCrsNodes;
	private int[] nodeMap;
	
	/**
     * Instantiate a new CoarseningInfo
     *
     * @param graph  graph
     */
	public CoarseningInfo(int nCrsNodes, int[] nodeMap) {
		this.nCrsNodes = nCrsNodes;
		this.nodeMap = nodeMap;
	}
	
	public int nCrsNodes() {
		return nCrsNodes;
	}
	
	public int nodeMap(int node) {
		return nodeMap[node];
	}
	
	public int[] nodeMap() {
		return nodeMap;
	}
}