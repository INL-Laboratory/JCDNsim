package entities.physical;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import entities.utilities.logger.Logger;
import sun.rmi.runtime.Log;

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
        for (EndDevice end : getVertices()) {
            if (end instanceof Server){
                Logger.print(((Server)end).toStringRoutingTable(), 0f);
            }
        }
    }
    //TODO: implement NumberOf Queries
    public List<Server> getNearestServers(int n, List<Server> preFilteredServers, EndDevice src){
        /***
         * returns the n nearest servers to the src in a list of servers that might have been already filtered.
         */
        List<Server> toReturnServers = new LinkedList<>();
        if (n<=0) return toReturnServers;

        Collections.sort(preFilteredServers, (Comparator<Server>) (o1, o2) -> {
            int o1Cost =  o1.getCommunicationCostTable().get(src);
            int o2Cost =  o2.getCommunicationCostTable().get(src);
            boolean con = o1Cost<o2Cost;
            boolean con2 = o1Cost>o2Cost;
            return con?-1:(con2?1:0);
            //TODO: check whether the order is right
        });
        for (int i = 0; i <n ; i++) {
            if (preFilteredServers.size()==i) return toReturnServers;
            toReturnServers.add(preFilteredServers.get(i));
        }
        return toReturnServers;
    }
    public Server getNearestServer(List<Server> preFilteredServers, Client src){
        /***
         * returns the nearest server to the client in a list of servers that might have been already filtered.
         */
        Server directlyConnectedServer = (Server)((Client) src).getLink().getOtherEndPoint(src);
        if (preFilteredServers.contains(directlyConnectedServer)) {
            return directlyConnectedServer;
        }
        List<Server> returnedByGeneralMethod = getNearestServers(1, preFilteredServers, src);
        if (returnedByGeneralMethod.size()==0) return null;
        return returnedByGeneralMethod.get(0);
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
        if (n<=0) return toReturnServers;
        Collections.sort(preFilteredServers, (Comparator<Server>) (o1, o2) -> {
            return o1.getServerLoad()<o2.getServerLoad()?1:0;
            //TODO: check whether the order is right
        });
        for (int i = 0; i <n ; i++) {
            toReturnServers.add(preFilteredServers.get(i));
        }
        return toReturnServers;
    }
    public Server getLeastLoadedServer(List<Server> preFilteredServers){
        /***
         * returns the least loaded server in a list of servers that might have been already filtered.
         */
        if (preFilteredServers.size()==0) return null;
        int minLoad= Integer.MAX_VALUE;
        Server toReturnServer = null;
        for (Server candidateServer:preFilteredServers) {
            if (candidateServer.getServerLoad()<minLoad){
                toReturnServer = candidateServer;
                minLoad = candidateServer.getServerLoad();

            }
        }
                //TODO
                if (minLoad>0){
                    int ss = 0;
                }
        return toReturnServer;
    }
    public Server getMostDesirableServer(List<Server> preFilteredServers , float alpha , EndDevice src){
        /***
         * returns the least desirable server in a list of servers that might have been already filtered.
         */
        if (preFilteredServers.size()==0) return null;
        double minDesirability = Double.MAX_VALUE;
        Server toReturnServer = null;
        int totalCosts = calculateTotalCosts(preFilteredServers, src);
        int totalLoad = calculateTotalLoad(preFilteredServers);
        double serverDesirability;
        for (Server candidateServer:preFilteredServers) {
             serverDesirability = calculateDesirability(totalCosts, totalLoad, candidateServer, alpha, src);
            if (serverDesirability<minDesirability){
                toReturnServer = candidateServer;
                minDesirability = serverDesirability;
            }
        }
        return toReturnServer;
    }

    private int calculateTotalCosts(List<Server> preFilteredServers, EndDevice src) {
        int sum = 0;
        for (Server server:preFilteredServers) {
            sum+=  server.getCommunicationCostTable().get(src);
        }
        return sum;
    }
    private int calculateTotalLoad(List<Server> preFilteredServers) {
        int sum = 0;
        for (Server server:preFilteredServers) {
            sum+= server.getServerLoad();
        }
        return sum;
    }

    private double calculateDesirability(int totalCost, int totalLoad, Server server, float alpha, EndDevice src) {
        int cost = server.getCommunicationCostTable().get(src);
        int load = server.getServerLoad();
        if (totalLoad==0) totalLoad = 1;
        double desirability = alpha * cost/totalCost + (1-alpha)*load/totalLoad;
        return desirability;
    }

}
