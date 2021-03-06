/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Network;

import com.sun.istack.internal.Nullable;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import entities.Setting.Configuration;
import entities.Simulator.RandomGeometricGraph;
import entities.Simulator.Topology;
import entities.Simulator.Vertex;
import org.apache.commons.collections15.Transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by hd on 2018/4/1 AD.
 * NetworkGraph is responsible for all actions related to graph of the network. It manages the routing tables,
 * returns the nearest servers, and returns the least loaded servers. It also is capable of creating adjacency matrix of topologies.
 */
public class NetworkGraph extends UndirectedSparseGraph<EndDevice,Link> {
    //it used to be like singleton design pattern
//    public NetworkGraph networkGraph = new NetworkGraph();

//    public void renewNetworrkGraph(){
//        networkGraph = new NetworkGraph();
//    }

    public NetworkGraph() {
    }

    public static Topology useRGG() {
        RandomGeometricGraph graph = RandomGeometricGraph.openGraph("graph-0.6-100-15-Jun-0006_11/40/25.dat");

        Hashtable<Vertex,List<Vertex>> adjacencyList = graph.adjacencyList;
        boolean[][] adjMat;
        int[][] weights;

        adjMat = new boolean[graph.n][graph.n];
        weights = new int[graph.n][graph.n];


        for (Vertex vertex:adjacencyList.keySet()) {
            for (Vertex neghbour:adjacencyList.get(vertex)) {
                if(!adjMat[neghbour.id][vertex.id])
                    adjMat[vertex.id][neghbour.id] = true;
                weights[vertex.id][neghbour.id] = (int)(graph.costNormalizationFactor*vertex.distanceFrom(neghbour));
            }
        }
        double sum=0;
        int numOfEdges=0;
        for (int i = 0; i < graph.n; i++) {
            for (int j = 0; j < graph.n ; j++) {
                if (adjMat[i][j]){
                    sum+=weights[i][j];
                    numOfEdges++;
                }
            }
        }
        float summ = (float) sum;
        float averageWeight = summ/numOfEdges;

//        for (int i = 0; i < weights.length; i++) {
//            for (int j = 0; j < weights[0].length; j++) {
//                weights[i][j] = (int)(weights[i][j]) ;
//            }
//        }

        Topology topology = new Topology(adjMat,weights,graph.costNormalizationFactor, averageWeight);
        return topology;
    }
public static boolean[][] usePowerLaw(Configuration configuration) {
    Scanner input = null;
    try {
        input = new Scanner(new File("powerLaw2"));
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }

        boolean[][] adjMat;
        int[][] weights;

        adjMat = new boolean[configuration.numberOfServers][configuration.numberOfServers];
        weights = new int[configuration.numberOfServers][configuration.numberOfServers];


    for (int i = 0; i < adjMat.length; i++) {
        if (input.hasNextLine())
        {
            for (int j = 0; j < adjMat[0].length ; j++) {
                if (input.hasNext())
                    if(input.next().equals("1")) {
                        adjMat[i][j] = true;
                    }
            }
        }
    }


    return adjMat;
}

    public static Topology getTopology(boolean[][] adjMat) {
        int[][] weights;
        weights = new int[adjMat.length][adjMat.length];

        double sum=0;
        int numOfEdges=0;
        for (int i = 0; i < adjMat.length; i++) {
            {
                for (int j = 0; j < adjMat[0].length ; j++) {
                        if(adjMat[i][j]) {
                            weights[i][j] = 1;
                        }
                }
            }
        }

        for (int i = 0; i < adjMat.length; i++) {
            for (int j = 0; j < adjMat[0].length ; j++) {
                if (adjMat[i][j]){
                    sum+=weights[i][j];
                    numOfEdges++;
                }
            }
        }
        float summ = (float) sum;
        float averageWeight = summ/numOfEdges;


        Topology topology = new Topology(adjMat,weights,1, averageWeight);
        return topology;
    }

    public void buildRoutingTables() {
        /***
         * Completely builds routing tables of all servers using Dijkestra shortest path algorithm
         */
        Server src;
        for (EndDevice end : getVertices()) {
            if (!(end instanceof Server)) continue;
            src = (Server) end;
            DijkstraShortestPath<EndDevice, Link> algorithm =
                    new DijkstraShortestPath<EndDevice, Link>(
                            this, new Transformer<Link, Number>() {
                        @Override
                        public Number transform(Link link) {
                            return link.getWeight();
                        }
                    }
                    );
//            Server dest;
            List<Link> path;
            Link link;
            for (EndDevice dest : getVertices()) {

//                dest = (Server) endd;
                if (dest == src) continue;
                if (src.getRoutingTable().get(dest) != null) continue;
                path = algorithm.getPath(src, dest);
                link = path.get(0);
                int communicationCost = 0;
                for (Link l : path) {
                    communicationCost += l.getWeight();
                }
                src.getRoutingTable().put(dest, link);
                src.getCommunicationCostTable().put(dest, communicationCost);
                //TODO: We should optimize this part if it was slow when exploiting the path list. Now we just use the first element of that

            }
        }
//        for (EndDevice end : getVertices()) {
//            if (end instanceof Server){
//                Logger.print(((Server)end).toStringRoutingTable(), 0f);
//            }
//        }
    }





    Map<Pair, List<Server>> cachSortedLists = new HashMap<>();
    Map<Pair, List<Server>> cachSortedListsNthPart = new HashMap<>();
    public int c = 0;
    public int t = 0;
    public List<Server> getNearestServers(int n, List<Server> preFilteredServers, final EndDevice src, @Nullable Random rnd){
        /***
         * returns the n nearest servers to the src in a list of servers that might have been already filtered.
         */
        List<Server> toReturnServers = new LinkedList<>();
        if (n<=0) return toReturnServers;
        List<Server> newList, newListN;
        newList = cachSortedLists.get(new Pair(src,preFilteredServers));
        newListN = cachSortedListsNthPart.get(new Pair(src,preFilteredServers));
        if (newListN!=null && newListN.size()!=0) {
            Collections.shuffle(newListN);
        }
//        if (n == 10 && newList!=null && newList.size()<n-1)
//            System.out.println();
//
        if (newList==null && newListN == null) {
            t++;
            newList = new LinkedList<>();
            newListN = new LinkedList<>();
            for (Server server : preFilteredServers) {
                newList.add(server);
            }
            Collections.shuffle(newList);

            Collections.sort(newList, new Comparator<Server>() {
                @Override
                public int compare(Server o1, Server o2) {
                    int o1Cost = o1.getCommunicationCostTable().get(src);
                    int o2Cost = o2.getCommunicationCostTable().get(src);
                    boolean con = o1Cost < o2Cost;
                    boolean con2 = o1Cost > o2Cost;
                    return con ? -1 : (con2 ? 1 : 0);
                }
            });

            if (n<newList.size()-1){
                int costNth= newList.get(n-1).getCommunicationCostTable().get(src);
                int nextCost= newList.get(n).getCommunicationCostTable().get(src);
                if( nextCost==costNth ) {
                    for (Server werver : newList) {
                        if (werver.getCommunicationCostTable().get(src).equals(costNth)) {
                            newListN.add(werver);
                        }
                    }
                    LinkedList<Server> toRemoveList = new LinkedList<>();
                    for (int i = newList.size() - 1; i >= 0; i--) {
                        toRemoveList.add(newList.get(i));
                        if (((LinkedList<Server>) newListN).getFirst() == newList.get(i)) {
                            break;
                        }
                    }
                    newList.removeAll(toRemoveList);
                }
            }

            cachSortedLists.put(new Pair( src,preFilteredServers), newList);
            cachSortedListsNthPart.put(new Pair( src,preFilteredServers), newListN);
        }else c++;



        if (newList!=null) {
            for (int i = 0; i < newList.size(); i++) {
                if (toReturnServers.size()>=n) break;
                toReturnServers.add(newList.get(i));
            }        }
        if (newListN!=null) {

            for (int i = 0; i < newListN.size(); i++) {
                if (toReturnServers.size()>=n) break;
                toReturnServers.add(newListN.get(i));
            }
        }
        if (rnd==null)
            Collections.shuffle(toReturnServers);
            else
                Collections.shuffle(toReturnServers,rnd);

        return toReturnServers;

    }


    /***
     * returns the nearest server to the client in a list of servers that might have been already filtered.
     */
    public Server getNearestServer(List<Server> preFilteredServers, Client src){
        if (preFilteredServers.size()==0) return null;
        Server directlyConnectedServer = (Server)src.getLink().getOtherEndPoint(src);
        if (preFilteredServers.contains(directlyConnectedServer)) {
            return directlyConnectedServer;
        }

        Server toReturnServer;
            int minCost = Integer.MAX_VALUE;
            for (Server candidateServer : preFilteredServers) {
                if (candidateServer.getCommunicationCostTable().get(src) < minCost) {
                    minCost = candidateServer.getCommunicationCostTable().get(src);
//                toReturnServer = candidateServer;
                }
            }
            List<Server> minLists = new ArrayList<>();
            for (Server candidateServer : preFilteredServers) {
                if (candidateServer.getCommunicationCostTable().get(src) == minCost) {
                    minLists.add(candidateServer);
                }
            }

            toReturnServer = minLists.get(new Random().nextInt(minLists.size()));
        return toReturnServer;
    }
    /***
     * returns the servers who have the file with fileId.
     */
    public List<Server> getServersHavingFile(int fileID){
        Server candidateServer;
        LinkedList<Server> toReturnServers = new LinkedList<>();
        for (EndDevice end:getVertices()) {
            if (!(end instanceof Server)) continue;
            candidateServer= (Server) end;
            if (candidateServer.findFile(fileID)!=null){
                toReturnServers.add(candidateServer);
            }
        }
        return toReturnServers;
    }
    /***
     * returns the sites who have the file with fileId.
     */
    public Map<Site,List<Server>> getSitesHavingFile(int fileID){
        Server candidateServer;
        Map<Site,List<Server>> toReturnSites = new HashMap<>();
        for (EndDevice end:getVertices()) {
            if (!(end instanceof Server)) continue;
            candidateServer= (Server) end;
            boolean hasTheFile = candidateServer.findFile(fileID)!=null;
            if ((!toReturnSites.containsKey(candidateServer.getSite())) && hasTheFile){
                toReturnSites.put(candidateServer.getSite(),new LinkedList<Server>());
            }
            if (hasTheFile){
                toReturnSites.get(candidateServer.getSite()).add(candidateServer);
            }

        }
        return toReturnSites;
    }
    /***
     * returns the n least loaded servers in a list of servers that might have been already filtered.
     */
    public List<Server> getLeastLoadedServers(int n, List<Server> preFilteredServers){
        List<Server> toReturnServers = new LinkedList<>();
//        if (n<=0) return toReturnServers;
//        Collections.sort(preFilteredServers, (Comparator<Server>) (o1, o2) -> {
//            return o1.getServerLoad()<o2.getServerLoad()?1:0;
//        });
//        for (int i = 0; i <n ; i++) {
//            toReturnServers.add(preFilteredServers.get(i));
//        }
        return toReturnServers;
    }
    /***
     * returns the least loaded server in a list of servers that might have been already filtered.
     */
    public Server getLeastLoadedServer(List<Server> preFilteredServers, Map<Server, Integer> serverLoads){
        if (preFilteredServers.size()==0) return null;
        int minLoad= Integer.MAX_VALUE;
        Server toReturnServer = null;
        for (Server candidateServer:preFilteredServers) {
            if (serverLoads.get(candidateServer)<minLoad){
                minLoad = serverLoads.get(candidateServer);
//                toReturnServer = candidateServer;
            }
        }
        List<Server> minLists = new ArrayList<>();
        for (Server candidateServer:preFilteredServers) {
            if (serverLoads.get(candidateServer)==minLoad){
                minLists.add(candidateServer);
            }
        }
        toReturnServer = minLists.get(new Random().nextInt(minLists.size()));

        return toReturnServer;
    }

    private Map<Pair,Integer> cachedTotalCost = new HashMap<>();

    /***
     * returns the least desirable server in a list of servers that might have been already filtered.
     */
    public HasLoadAndCost getMostDesirableServerOrSite(List<HasLoadAndCost> preFilteredS, Map<HasLoadAndCost,Integer> loads, double alpha, EndDevice src){

//        Logger.printWithoutTime("*******Servers Having File:");
        if (preFilteredS.size()==0) return null;
        double minDesirability = Double.MAX_VALUE;
        HasLoadAndCost toReturnS = null;
        Integer totalCosts;
        totalCosts = getTotalCost(preFilteredS, src);
        int totalLoad = calculateTotalLoad(preFilteredS,loads);

//        Logger.printWithoutTime(" total cost = "+ totalCosts + "  total Load = " + totalLoad);
        double sDesirability;
        for (HasLoadAndCost candidateS:preFilteredS) {
            sDesirability = calculateDesirability(totalCosts, totalLoad, candidateS,loads.get(candidateS), alpha, src);
//            Logger.printWithoutTime(candidateS.toString()+" queueSize = "+loads.get(candidateS)+ " cost = " + candidateS.getCommunicationCostTable().get(src) + "desirability = " + serverDesirability);
            if (Double.compare(sDesirability,minDesirability)==-1){
//                toReturnS = candidateS;
                minDesirability = sDesirability;
            }
        }

        List<HasLoadAndCost> minLists = new ArrayList<>();
        for (HasLoadAndCost candidateS:preFilteredS) {
            if (Double.compare(calculateDesirability(totalCosts, totalLoad, candidateS, loads.get(candidateS), alpha, src),minDesirability)==0){
                minLists.add(candidateS);
            }
        }
        toReturnS = minLists.get(new Random().nextInt(minLists.size()));
//        Logger.printWithoutTime(" total cost = "+ totalCosts + "  total Load = " + totalLoad);
        return toReturnS;
    }

    private Integer getTotalCost(List preFilteredS, EndDevice src) {
        Integer totalCosts;
        totalCosts = cachedTotalCost.get(new Pair(src,preFilteredS));
        if(totalCosts== null){
            totalCosts = calculateTotalCosts(preFilteredS, src);
            cachedTotalCost.put(new Pair(src,preFilteredS),totalCosts);
        }
        return totalCosts;
    }

    private int calculateTotalCosts(List<HasLoadAndCost> preFilteredS, EndDevice src) {
        int sum = 0;
        for (HasLoadAndCost serverOrsite:preFilteredS) {
            sum+=  serverOrsite.getCommunicationCostTable().get(src);
        }
        return sum;
    }
    private int calculateTotalLoad(List<HasLoadAndCost> preFilteredS, Map<HasLoadAndCost, Integer> loads) {
        int sum = 0;
        for (HasLoadAndCost serverOrSite:preFilteredS) {
            sum+= loads.get(serverOrSite);
        }
        return sum;
    }

    private double calculateDesirability(int totalCost, int totalLoad, HasLoadAndCost servOrsite, Integer load, double alpha, EndDevice src) {
        int cost = servOrsite.getCommunicationCostTable().get(src);
        if (totalLoad==0) totalLoad = 1;
        if (totalCost==0) totalCost = 1;
        double desirability = alpha *( ((double)cost)/totalCost) + (1-alpha)*(((double)load)/totalLoad);
        return desirability;
    }

    public List<Server> getNearerServers(int D, List<Server> preFilteredServers, Client src) {
        if (preFilteredServers.size()==0) return null;
        Collections.shuffle(preFilteredServers);
        ArrayList<Server> toReturnServers = new ArrayList<>();
        Server connectedServer = (Server) src.getLink().getOtherEndPoint(src);
        for (Server candidateServer:preFilteredServers) {
            if (connectedServer.equals(candidateServer) ) toReturnServers.add(candidateServer);
            else if (connectedServer.getCommunicationCostTable().get(candidateServer)<=D){
                  toReturnServers.add(candidateServer);
            }
        }

        return toReturnServers;

    }

    public static boolean[][] generateLatticeTopology(int n) {
        boolean[][] top = new boolean[n][n];
        int m = (int)Math.sqrt(n);
        if (Math.sqrt(n)!=m) throw new RuntimeException("Square number of servers is needed");
        for (int i = 0; i < n ; i++) {
            int[] neighbours= {i%m==0?i+m-1:i-1
                    ,i%m==m-1?i-m+1:i+1
                    ,i<m?n-m+i:i-m
                    ,n-1-i<m?i-(n-m):i+m};
            for (int j = 0; j < neighbours.length ; j++) {
                if (neighbours[j]<i) continue;
                top[i][neighbours[j]] =  true;
            }
        }
        return top;
    }


}
