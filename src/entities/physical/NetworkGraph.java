package entities.physical;

import com.sun.istack.internal.Nullable;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import entities.logical.Pair;
import entities.utilities.logger.Logger;
import org.apache.commons.collections15.Transformer;

import java.util.*;

/**
 * Created by hd on 2018/4/1 AD.
 */
public class NetworkGraph extends UndirectedSparseGraph<EndDevice,Link> {
    //it used to be like singleton design pattern
//    public NetworkGraph networkGraph = new NetworkGraph();

//    public void renewNetworrkGraph(){
//        networkGraph = new NetworkGraph();
//    }

    public NetworkGraph() {
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
    public Map<Site,List<Server>> getSitesHavingFile(int fileID){
        /***
         * returns the sites who have the file with fileId.
         */
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
//    static int maxLoad = 0;

    private Map<Pair,Integer> cachedTotalCost = new HashMap<>();


    public HasLoadAndCost getMostDesirableServerOrSite(List<HasLoadAndCost> preFilteredS, Map<HasLoadAndCost,Integer> loads, double alpha, EndDevice src){
        /***
         * returns the least desirable server in a list of servers that might have been already filtered.
         */
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


//        System.out.println(minDesirability);
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

    public List<Server> getNearerServers(int D, List<Server> preFilteredServers, Client src, Random rnd) {
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
}
