import java.util.*;

/**
 * Created by Lily on 11/26/16.
 */
public class Graph {

    private Set<List<Integer>> teams;
    private int size;
    private HashMap<Integer, ArrayList<Integer>> adjacencyList;
    private int[] probs;
    private Set<Integer> startingVertices;
    private HashSet<Integer> remaining;
    ArrayList<Double> weights = new ArrayList<Double>();
    public double punishment = 1.1;
    
    private ArrayList<Integer> lengthTo; //CHANGED
    private ArrayList<Integer> prev; //CHANGED
    
    protected Graph(Graph another) {
        this.size = another.size; // you can access
        this.adjacencyList = another.adjacencyList;
        probs = another.probs;
        teams = new HashSet<List<Integer>> ();
        startingVertices = new HashSet<Integer>();
        remaining = new HashSet<Integer>(another.remaining);
        lengthTo=another.lengthTo;
        prev=another.prev;
    }

    protected Graph(int size) {
        this.size = size;
        adjacencyList = new HashMap<Integer, ArrayList<Integer>>();
        probs = new int[size];
        for (int i = 0; i < size; i++) {
            adjacencyList.put(i, new ArrayList<Integer>());
        }
        teams = new HashSet<List<Integer>> ();
        startingVertices = new HashSet<Integer>();
        remaining = new HashSet<Integer>();
        for (int i = 0; i<getSize(); i++) {
            remaining.add(i);
        }
        lengthTo = new ArrayList<Integer>();
        for (int i = 0; i < getSize(); i++) {
        	lengthTo.add(0);
        }
        prev = new ArrayList<Integer>();
        for (int i = 0; i < getSize(); i++) {
        	prev.add(-1);
        }
    }
    protected void addEdge(int from, int to) {
        this.adjacencyList.get(from).add(to);
    }

    protected ArrayList<Integer> getAdjacentEdges(int start) {
        return this.adjacencyList.get(start);
    }

    protected void addProb(int i, int prob) {
        this.probs[i]=prob;
    }

    protected int getSize() {return this.size;}

    protected int getProb(int node) {
        return this.probs[node];
    }

    /*
    protected PriorityQueue<Integer> getSortedProbQueue() {
        PriorityQueue<Integer> probQueue = new PriorityQueue<Integer>(new PQsort());
        return probQueue;
    }
*/
    //如果一个team首尾相连，就算cycle。
    protected boolean isCycle(List<Integer> team) {
        int lastV = team.get(team.size() - 1);
        List<Integer> adj = this.getAdjacentEdges(lastV);
        return adj.contains(team.get(0));
    }

    /** Precondition : To is a neighboring state of FROM.
     * Return : TRUE if merged. */
    protected boolean mergeIfPossible(int from, int to, List<Integer> soFar) {
        for (List<Integer> team : teams) {
            for (int t : team) {
                if (t == to) {
                    //太屌了。。。
                    if (isCycle(team)) {
                        mergeCycle(from, to, team, soFar);
                        return true;
                    } else {
                        if (team.get(0)==to) {//is a starting vertex
                            mergePath(from, to, team, soFar);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
//
    protected void mergePath(int from, int to, List<Integer> team, List<Integer> soFar) {
        teams.remove(team);;
        soFar.addAll(team);
    }

    protected void mergeCycle(int from, int to, List<Integer> team, List<Integer> soFar) {
        teams.remove(team);
        boolean startAdding = false;
        for (int T : team) {
            if (T==to) {
                startAdding = true;
            }
            if (startAdding) {
                soFar.add(T);
            }
        }
        for (int T : team) {
            if (T==to) {
                return;
            }
            soFar.add(T);
        }
    }

    /** Returns TRUE iff. Something has been merged*/
    protected void findRandomPathWithMerging(int start, List<Integer> soFar) {
        soFar.add(start);
        List<Integer> adj = getAdjacentEdges(start);
        Set<Integer> path;
        int nextVertex = getRandomVertex(adj);
        if (nextVertex ==-1) {
            return;
        }
        for (int s : soFar) {
            if (s == nextVertex) {
                return;
            }
        }
        if (remaining.contains(nextVertex)) {
            findRandomPathWithMerging(nextVertex, soFar);
        } else {
            mergeIfPossible(start, nextVertex, soFar);
        }
    }


    private static int getRandomVertex(Collection<Integer> from) {

        if (from.size()==0) {
            return -1;
        }
        Random rnd = new Random();
        int i = rnd.nextInt(from.size());
        /** Explore a unexplored path*/
        return (int)from.toArray()[i];

    }

    protected Set<List<Integer>> getAPermutation(String keyword) {
        while (!remaining.isEmpty()) {
            int V = new Random().nextInt(getSize());
            while (!remaining.contains(V)) {
                V = new Random().nextInt(getSize());
            }
            List<Integer> newPath = new ArrayList<Integer>();
            if (keyword.equals("RANDOM")) {
                findRandomPath(V, newPath);
            } else if (keyword.equals("RANDOM MERGE")) {
                findRandomPathWithMerging(V, newPath);
            } else if (keyword.equals("GREEDY")) {
                findGreedyPath(V, newPath);
            } else if (keyword.equals("LONG")) {
            	findLongPath(V);
            	int length = 0;
            	int node = 0;
            	for (int i = 0; i < lengthTo.size(); i++) {
            		if (lengthTo.get(i) > length) {
            			node = i;
            			length = lengthTo.get(i);
            		}
            	} 
            	if (length > 0) {
            		while (node != -1) {
                		newPath.add(node);
                		node = prev.get(node);
                	}
                	Collections.reverse(newPath);
            	} else {
            		newPath.add(V);
            	}
            	lengthTo = new ArrayList<Integer>();
            	for (int i = 0; i < getSize(); i++) {
            		lengthTo.add(0);
            	}
            	prev = new ArrayList<Integer>();
            	for (int i = 0; i < getSize(); i++) {
            		prev.add(-1);
            	}
            }
            for (int n : newPath) {
                remaining.remove(n);
            }
            teams.add(newPath);
        }
        return teams;
    }

    private void findLongPath(int startVertex) {
		// TODO Auto-generated method stub
    	if (getAdjacentEdges(startVertex).size()==0) {
            return;
        } else {
            ArrayList<Integer> neighbors = getAdjacentEdges(startVertex);
            for (int n : neighbors) {
            	if (remaining.contains(n)) {
            		if (lengthTo.get(n) <= lengthTo.get(startVertex) + 1) {
            			lengthTo.set(n, lengthTo.get(startVertex) + 1);
            			prev.set(n, startVertex);
            		}
            		findLongPath(n);
            	}
            }
        }
	}

	protected void findRandomPath( int start, List<Integer> soFar) {
        soFar.add(start);
        List<Integer> adj = getAdjacentEdges(start);
        int nextVertex = getRandomVertex(adj);
        if (nextVertex ==-1) {
            return;
        }
        for (int s : soFar) {
            //如果之前有见过这个node，这个node不加进team里，team就return。
            if (s == nextVertex) {
                return;
            }
        }
        if (remaining.contains(nextVertex)) {
            findRandomPath(nextVertex, soFar);
        }
    }
    // Greedy Algorithm for choosing neighbor:
    protected void findGreedyPath(int startVertex, List<Integer> soFar) {
        soFar.add(startVertex);
        if (getAdjacentEdges(startVertex).size()==0) {
            return;
        } else {
            ArrayList<Integer> neighbors = getAdjacentEdges(startVertex);
            int maxNeighbor = chooseMax(neighbors);
            boolean seen = false;
            if (this.remaining.contains(maxNeighbor)) {
                for (int s : soFar) {
                    if (s == maxNeighbor) {
                        seen = true;
                    }
                }
            } else {
                //代表另一个队用了，所以才会从remaining里找不到。
                boolean merged = mergeIfPossible(startVertex, maxNeighbor, soFar);
                if (merged) {
                    //如果可以merge，merge起来就return。
                    return;
                }
                //如果不能merge，但是这样也说明别的队伍之前seen了。
                seen = true;
            }

            //while seen为true 能run到这里只能是之前想merge并不能merge或者是DFS 选的node之前走过了，有一个cycle
            while (seen) {
                if (maxNeighbor==-1) {
                    return;
                }
                //DFS的时候换一个node深入，选第二大的。
                neighbors.remove((Object)maxNeighbor);
                maxNeighbor = chooseMax(neighbors);
                seen = false;
                if (this.remaining.contains(maxNeighbor)) {
                    for (int s : soFar) {
                        if (s == maxNeighbor) {
                            seen = true;
                        }
                    }
                } else {
                    boolean merged = mergeIfPossible(startVertex, maxNeighbor, soFar);
                    if (merged) {
                        return;
                    }
                    seen = true;
                }
            }
            //直到碰到没见过的node，然后继续dfs组team
            if (maxNeighbor != -1) {
                findGreedyPath(maxNeighbor, soFar);
            } else {
                return;
            }
        }
    }
    protected int chooseMax(ArrayList<Integer> nodes) {
        if (nodes.size() == 0) {
            return -1;
        }
        int maxProb = Integer.MIN_VALUE;
        int maxNeighbor = 0;
        for (int i = 0; i < nodes.size(); i++) {
            int currProb = getProb(nodes.get(i));
            if (currProb > maxProb) {
                maxProb = currProb;
                maxNeighbor = nodes.get(i);
            }
        }
        return maxNeighbor;
    }


    // Calculate total score from current teams:
    protected int totalScore () {
        int total = 0;
        for (List<Integer> L : teams) {
            List<Integer> currTeam = L;
            int subSum = 0;
            for (int j = 0; j < currTeam.size(); j++) {
                subSum += getProb(currTeam.get(j));
            }
            total += currTeam.size() * subSum;
        }
        return total;
    }


}

