package entities.physical;

import com.sun.istack.internal.Nullable;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import entities.logical.Pair;
import entities.utilities.logger.Logger;

import java.util.*;

/**
 * Created by hd on 2018/4/1 AD.
 */
public class NetworkGraph extends UndirectedSparseGraph<EndDevice,Link> {
    //it's like singleton design pattern
    public static NetworkGraph networkGraph = new NetworkGraph();

    public static void renewNetworrkGraph(){
        networkGraph = new NetworkGraph();
    }

    private NetworkGraph() {
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
                            this, link -> link.getWeight()
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
                //TODO: We should optimize this part if it was slow by exploiting the path list. Now we just use the first element of that
                //TODO: We should test whether the first link is at index 0 or the last one

            }
        }
//        for (EndDevice end : getVertices()) {
//            if (end instanceof Server){
//                Logger.print(((Server)end).toStringRoutingTable(), 0f);
//            }
//        }
    }
    //TODO: implement NumberOf Queries





    Map<Pair, List<Server>> cachSortedLists = new HashMap<>();
    Map<Pair, List<Server>> cachSortedListsNthPart = new HashMap<>();
    public int c = 0;
    public int t = 0;
    public List<Server> getNearestServers(int n, List<Server> preFilteredServers, EndDevice src, @Nullable Random rnd){
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

            Collections.sort(newList, (Comparator<Server>) (o1, o2) -> {
                int o1Cost = o1.getCommunicationCostTable().get(src);
                int o2Cost = o2.getCommunicationCostTable().get(src);
                boolean con = o1Cost < o2Cost;
                boolean con2 = o1Cost > o2Cost;
                return con ? -1 : (con2 ? 1 : 0);
                //TODO: check whether the order is right
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



    public Server getNearestServer(List<Server> preFilteredServers, Client src){
        /***
         * returns the nearest server to the client in a list of servers that might have been already filtered.
         */
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
    public List<Server> getServersHavingFile(int fileID){
        /***
         * returns the servers who have the file with fileId.
         */
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
    public List<Server> getLeastLoadedServers(int n, List<Server> preFilteredServers){
        /***
         * returns the n least loaded servers in a list of servers that might have been already filtered.
         */
        List<Server> toReturnServers = new LinkedList<>();
//        if (n<=0) return toReturnServers;
//        Collections.sort(preFilteredServers, (Comparator<Server>) (o1, o2) -> {
//            return o1.getServerLoad()<o2.getServerLoad()?1:0;
//            //TODO: check whether the order is right
//        });
//        for (int i = 0; i <n ; i++) {
//            toReturnServers.add(preFilteredServers.get(i));
//        }
        return toReturnServers;
    }
    public Server getLeastLoadedServer(List<Server> preFilteredServers, Map<Server, Integer> serverLoads){
        /***
         * returns the least loaded server in a list of servers that might have been already filtered.
         */
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
    static int maxLoad = 0;

    private Map<Pair,Integer> cachedTotalCost = new HashMap<>();


    public Server getMostDesirableServer(List<Server> preFilteredServers, Map<Server, Integer> serverLoads, double alpha, EndDevice src){
        /***
         * returns the least desirable server in a list of servers that might have been already filtered.
         */
//        Logger.printWithoutTime("*******Servers Having File:");
        if (preFilteredServers.size()==0) return null;
        double minDesirability = Double.MAX_VALUE;
        Server toReturnServer = null;
        Integer totalCosts;
        totalCosts = cachedTotalCost.get(new Pair(src,preFilteredServers));
        if(totalCosts== null){
            totalCosts = calculateTotalCosts(preFilteredServers, src);
            cachedTotalCost.put(new Pair(src,preFilteredServers),totalCosts);
        }
        int totalLoad = calculateTotalLoad(preFilteredServers,serverLoads);

//        Logger.printWithoutTime(" total cost = "+ totalCosts + "  total Load = " + totalLoad);
        double serverDesirability;
        for (Server candidateServer:preFilteredServers) {
            serverDesirability = calculateDesirability(totalCosts, totalLoad, candidateServer,serverLoads.get(candidateServer), alpha, src);
//            Logger.printWithoutTime(candidateServer.toString()+" queueSize = "+serverLoads.get(candidateServer)+ " cost = " + candidateServer.getCommunicationCostTable().get(src) + "desirability = " + serverDesirability);
            if (Double.compare(serverDesirability,minDesirability)==-1){
//                toReturnServer = candidateServer;
                minDesirability = serverDesirability;
            }
        }

        List<Server> minLists = new ArrayList<>();
        for (Server candidateServer:preFilteredServers) {
            if (Double.compare(calculateDesirability(totalCosts, totalLoad, candidateServer, serverLoads.get(candidateServer), alpha, src),minDesirability)==0){
                minLists.add(candidateServer);
            }
        }
        toReturnServer = minLists.get(new Random().nextInt(minLists.size()));


//        System.out.println(minDesirability);
//        Logger.printWithoutTime(" total cost = "+ totalCosts + "  total Load = " + totalLoad);
        return toReturnServer;
    }

    private int calculateTotalCosts(List<Server> preFilteredServers, EndDevice src) {
        int sum = 0;
        for (Server server:preFilteredServers) {
            sum+=  server.getCommunicationCostTable().get(src);
        }
        return sum;
    }
    private int calculateTotalLoad(List<Server> preFilteredServers, Map<Server, Integer> serverLoads) {
        int sum = 0;
        for (Server server:preFilteredServers) {
            sum+= serverLoads.get(server);
        }
        return sum;
    }

    private double calculateDesirability(int totalCost, int totalLoad, Server server, Integer load, double alpha, EndDevice src) {
        int cost = server.getCommunicationCostTable().get(src);
        if (totalLoad==0) totalLoad = 1; if (totalCost==0) totalCost = 1;
        double desirability = alpha *( ((double)cost)/totalCost) + (1-alpha)*(((double)load)/totalLoad);
        return desirability;
    }

}
