import java.util.*;
import edu.princeton.cs.algs4.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.*;


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
class LabNetworkCabling {
    private EdgeWeightedGraph weightedGraph;
    private double[][] minimumSpanningTree;
//    private KruskalMST mst;
    private PrimMST mst;
    private int server;
    private int router;
    private List<Integer> computers;
    private List<Integer> printers;
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
        minimumSpanningTree = new double[weightedGraph.V()][weightedGraph.V()];
        for (int[] i : links) weightedGraph.addEdge(new Edge(i[0], i[1], i[2]));
        mst = new PrimMST(weightedGraph);
//        mst = new KruskalMST(weightedGraph);
        for (Edge e : mst.edges()) {
            int u = e.either();
            int v = e.other(u);
            minimumSpanningTree[u][v] = e.weight();
            minimumSpanningTree[v][u] = e.weight();
        }
    };

    public int cablingCost() {
        int cost = 0;
        // calculate the total cost
        cost = (int)mst.weight();
        return cost;
    }

    private void dfs(int src, boolean[] marked, int[] edgeTo) {
        marked[src] = true;
        for (int i = 0; i < minimumSpanningTree.length; i++) {
            if (minimumSpanningTree[src][i] != 0) {
                if (!marked[i]) {
                    dfs(i, marked, edgeTo);
                    edgeTo[i] = src;
                }
            }
        }
    }

    public int findPathWeight(int src, int dst) {
        boolean[] marked = new boolean[minimumSpanningTree.length];
        int[] edgeTo = new int[minimumSpanningTree.length];
        dfs(src, marked, edgeTo);
        double totalWeight = 0;
        while (dst != src) {
            totalWeight += minimumSpanningTree[dst][edgeTo[dst]];
            dst = edgeTo[dst];
        }
        return (int) totalWeight;
    }

    public int serverToRouter(){
        int srDistance = 0;
        // find the path distance between the server and the router
        srDistance = findPathWeight(server, router);
        return srDistance;
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