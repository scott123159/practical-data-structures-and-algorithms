import java.util.*;
import edu.princeton.cs.algs4.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.*;
import edu.princeton.cs.algs4.Stack;


class OutputFormat{
    LabNetworkCabling l;
    Map<Integer, String> deviceTypes;
    List<int[]> links;

    int cablingCost;
    int serverToRouter;
    int mostPopularPrinter;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}

class test_LabNetworkCabling{
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        int num_ac = 0;

        try {
            TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);

            for(int i = 0; i<testCases.length;++i)
            {
                for (OutputFormat data : testCases[i].data) {

                    LabNetworkCabling LNC = new LabNetworkCabling(data.deviceTypes, data.links);
                    int ans_cc = data.cablingCost;
                    int ans_sr = data.serverToRouter;
                    int ans_mpp = data.mostPopularPrinter;

                    int user_cc = LNC.cablingCost();
                    int user_sr = LNC.serverToRouter();
                    int user_mpp = LNC.mostPopularPrinter();

                    if(user_cc == ans_cc && user_sr == ans_sr && user_mpp==ans_mpp)
                    {
                        System.out.println("AC");
                        num_ac++;
                    }
                    else
                    {
                        System.out.println("WA");
                        System.out.println("Input deviceTypes:\n" + data.deviceTypes);
                        System.out.println("Input links: ");
                        for (int[] link : data.links) {
                            System.out.print(Arrays.toString(link));
                        }

                        System.out.println("\nAns cablingCost: " + ans_cc );
                        System.out.println("Your cablingCost:  " + user_cc);
                        System.out.println("Ans serverToRouter:  " + ans_sr);
                        System.out.println("Your serverToRouter:  " + user_sr);
                        System.out.println("Ans mostPopularPrinter:  " + ans_mpp);
                        System.out.println("Your mostPopularPrinter:  " + user_mpp);
                        System.out.println("");
                    }
                }
            }
            System.out.println("Score: "+num_ac+"/10");
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class Dijkstra {
    public double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every
     * other vertex in the edge-weighted graph {@code G}.
     *
     * @param  G the edge-weighted digraph
     * @param  s the source vertex
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public Dijkstra(EdgeWeightedGraph G, int s) {
        for (Edge e : G.edges()) {
            if (e.weight() < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }

        distTo = new double[G.V()];
        edgeTo = new Edge[G.V()];

        validateVertex(s);

        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : G.adj(v))
                relax(e, v);
        }

        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    /**
     * Returns the length of a shortest path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return the length of a shortest path between the source vertex {@code s} and
     *         the vertex {@code v}; {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    /**
     * Returns true if there is a path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return {@code true} if there is a path between the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path between the source vertex {@code s} and vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return a shortest path between the source vertex {@code s} and vertex {@code v};
     *         {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Edge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e = v-w:            distTo[w] <= distTo[v] + e.weight()
    // (ii) for all edge e = v-w on the SPT: distTo[w] == distTo[v] + e.weight()
    private boolean check(EdgeWeightedGraph G, int s) {

        // check that edge weights are non-negative
        for (Edge e : G.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v-w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (Edge e : G.adj(v)) {
                int w = e.other(v);
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v-w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            if (w != e.either() && w != e.other(e.either())) return false;
            int v = e.other(w);
            if (distTo[v] + e.weight() != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Unit tests the {@code DijkstraUndirectedSP} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        EdgeWeightedGraph G = new EdgeWeightedGraph(in);
        int s = Integer.parseInt(args[1]);

        // compute shortest paths
        edu.princeton.cs.algs4.DijkstraUndirectedSP sp = new edu.princeton.cs.algs4.DijkstraUndirectedSP(G, s);


        // print shortest path
        for (int t = 0; t < G.V(); t++) {
            if (sp.hasPathTo(t)) {
                StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
                for (Edge e : sp.pathTo(t)) {
                    StdOut.print(e + "   ");
                }
                StdOut.println();
            }
            else {
                StdOut.printf("%d to %d         no path\n", s, t);
            }
        }
    }

}
class LabNetworkCabling {
    private EdgeWeightedGraph weightedGraph;
    private EdgeWeightedGraph minimumSpanningTree;
//    private KruskalMST mst;
    private PrimMST mst;
    private int server;
    private int router;
    private List<Integer> computers;
    private List<Integer> printers;
    private double[][] weightOfDijkstra;
    public LabNetworkCabling(Map<Integer, String> deviceTypes, List<int[]> links){
        // create a Minimum Spanning Tree
        computers = new ArrayList<>();
        printers = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
            if (Objects.equals(entry.getValue(), "Server")) server = entry.getKey();
            if (Objects.equals(entry.getValue(), "Router")) router = entry.getKey();
            if (Objects.equals(entry.getValue(), "Computer")) computers.add(entry.getKey());
            if (Objects.equals(entry.getValue(), "Printer")) printers.add(entry.getKey());
        }
        weightedGraph = new EdgeWeightedGraph(deviceTypes.size());
        minimumSpanningTree = new EdgeWeightedGraph(weightedGraph.V());
        weightOfDijkstra = new double[weightedGraph.V()][weightedGraph.V()];
        for (int[] i : links) weightedGraph.addEdge(new Edge(i[0], i[1], i[2]));
        mst = new PrimMST(weightedGraph);
//        mst = new KruskalMST(weightedGraph);
        for (Edge e : mst.edges()) minimumSpanningTree.addEdge(e);
        for (int i : printers) {
            Dijkstra dijkstra = new Dijkstra(minimumSpanningTree, i);
            weightOfDijkstra[i] = dijkstra.distTo;
        }
    };

    public int cablingCost() {
        int cost = 0;
        // calculate the total cost
        cost = (int)mst.weight();
        return cost;
    }

    public int findPathWeight(int src, int dst) {
//        DepthFirstPaths dfs = new DepthFirstPaths(minimumSpanningTree, src);
////        BreadthFirstPaths bfs = new BreadthFirstPaths(minimumSpanningTree, src);
//        double totalWeight = 0;
//        Stack<Integer> path = (Stack<Integer>) dfs.pathTo(dst);
////        Stack<Integer> path = (Stack<Integer>) bfs.pathTo(dst);
//        while (path.size() > 1) {
//            int u = path.pop();
//            totalWeight += weightOfSpanningTree[u][path.peek()];
//        }
//        return (int) totalWeight;
//        Dijkstra dijkstra = new Dijkstra(minimumSpanningTree, src);
//        return (int) dijkstra.distTo(dst);
        return (int) weightOfDijkstra[dst][src];
    }

    public int serverToRouter(){
//        int srDistance = 0;
//        // find the path distance between the server and the router
//        srDistance = findPathWeight(server, router);
        Dijkstra dijkstra = new Dijkstra(minimumSpanningTree, server);
        return (int) dijkstra.distTo(router);
    }

    public int mostPopularPrinter(){
        Map<Integer, Integer> frequency = new HashMap<>();
        int printerIndex = Integer.MIN_VALUE;
        int maximumConnection = Integer.MIN_VALUE;
        for (int computer : computers) {
            int localMinimumWeight = Integer.MAX_VALUE;
            int localPrinterIndex = Integer.MAX_VALUE;
            for (int printer : printers) {
                int weight = findPathWeight(computer, printer);
                if (weight == localMinimumWeight) localPrinterIndex = Math.min(localPrinterIndex, printer);
                if (weight < localMinimumWeight) {
                    localPrinterIndex = printer;
                    localMinimumWeight = weight;
                }
            }
            frequency.put(localPrinterIndex, frequency.getOrDefault(localPrinterIndex, 0) + 1);
        }
        // find the most popular printer and return its index
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() == maximumConnection) printerIndex = Math.min(printerIndex, entry.getKey());
            if (entry.getValue() > maximumConnection) {
                printerIndex = entry.getKey();
                maximumConnection = entry.getValue();
            }
        }
        return printerIndex;
    }

    public static void main(String[] args) {

        // [device index, device type]
        Map<Integer, String> deviceTypes = Map.of(
                0, "Router",
                1, "Server",
                2, "Printer",
                3, "Printer",
                4, "Computer",
                5, "Computer",
                6, "Computer"
        );

        // [device a, device b, link distance (cable length)]
        List<int[]> links = List.of(
                new int[]{0, 1, 4},
                new int[]{1, 2, 2},
                new int[]{2, 4, 1},
                new int[]{0, 3, 3},
                new int[]{1, 3, 8},
                new int[]{3, 5, 7},
                new int[]{3, 6, 9},
                new int[]{0, 6, 5}
        );

        LabNetworkCabling Network = new LabNetworkCabling(deviceTypes, links);
        System.out.println("Total Cabling Cost: " + Network.cablingCost());
        System.out.println("Distance between Server and Router: " + Network.serverToRouter());
        System.out.println("Most Popular Printer: " + Network.mostPopularPrinter());
    }
}