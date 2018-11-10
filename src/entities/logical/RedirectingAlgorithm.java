package entities.logical;

import entities.physical.*;

import java.util.*;


/**
 * Created by hd on 2018/4/2 AD.
 * Redirecting algorithms are implemented here.
 */
public class RedirectingAlgorithm {
    float queryDelay = 0;
    public Random rnd = new Random();
    public NetworkGraph networkGraph;
    public AlgorithmData algorithmData;
//    public static double totalTime = 0;


    public RedirectingAlgorithm(AlgorithmData algorithmData) {
        this.algorithmData = algorithmData;
    }


    /**
     * This method identifies the algorithm called and calls the associated algorithm method
     * @param redirectingAlgorithmType Specifies the algorithm name.
     * @param serversHavingFile Servers which have the specified file
     * @param serverLoads Current recorded loads for the severs based on the list of the server requesting a suitable server to redirect
     * @param client The client originally making the request
     * @param serverShares = other server's loads - the load of server calling this method
     * @param sitesHavingFile = sites having the specified file
     * @return The most suitable server according to the selected algorithm
     */
    public Server selectServerToRedirect(RedirectingAlgorithmType redirectingAlgorithmType, List<Server> serversHavingFile,
                                         Map<Server, Integer> serverLoads, Client client, Map<Server,
                                         Integer> serverShares, Map<Site,List<Server>> sitesHavingFile){
//        double a = System.currentTimeMillis();

        Server selectedServer;
//        queryDelay = 0;
        switch (redirectingAlgorithmType){
            case PSS:
                selectedServer = selectPSSserver(client, serversHavingFile,serverLoads);
                break;
            case WMC:
                selectedServer = (Server) selectWMCServerOrSite(client, (List)serversHavingFile,(Map)serverLoads);
                break;
            case MCS:
                selectedServer = selectMCSserver(client, serversHavingFile,serverLoads);
                break;
            case CostBased:
                selectedServer = selectCostBasedserver(client, serversHavingFile,serverShares,serverLoads);
                break;
            case HONEYBEE:
                selectedServer = selectHoneyBeeServer2(client ,serversHavingFile,sitesHavingFile,serverLoads);
                break;
            default:
                throw new RuntimeException("Redirecting Algorithm is not defined");
        }

//        totalTime += System.currentTimeMillis()-a;
        return selectedServer;
    }

    private Server selectWMCsiteThenServer(Client client, List<Server> serversHavingFile, Map<Server,Integer> serverLoads, List<Site> sitesHavingFile) {
        Site selectedSite = (Site)selectWMCServerOrSite(client,(List)sitesHavingFile,(Map)getTotalSiteLoads(serverLoads,sitesHavingFile));
        List<Server> serverList = new LinkedList<>();
        for (Server server:selectedSite.getServers()) {
            if (serversHavingFile.contains(server)){
                serverList.add(server);
            }
        }
        if (serverList.size()==0) throw new RuntimeException();
        Server selectedServer = (Server) selectWMCServerOrSite(client,(List)serverList,(Map)serverLoads);
        return selectedServer;
    }

    private Server selectWMCsiteThenServer2(Client client, Map<Server,Integer> serverLoads, Map<Site,List<Server>> sitesHavingFile) {
        Map<Site,Server> theBestServersInEachSite = findTheBestServerInEachSite(client,serverLoads,sitesHavingFile);
        List bestServersInSites = new LinkedList();
        for (Server server:theBestServersInEachSite.values()) {
            bestServersInSites.add(server);
        }
        Server selectedServer = (Server) selectWMCServerOrSite(client,bestServersInSites,(Map)serverLoads);

        return selectedServer;
    }


    private int calculateTotalLoad(Map<Server,Integer> serverLoads, Site site){
        int size = site.getServers().size();
        int sum = 0;
        for (Server server:site.getServers()) {
            sum+=serverLoads.get(server);
        }
        return sum;
    }
    private Map<Site,Integer> getTotalSiteLoads(Map<Server,Integer> serverLoads, List<Site> sites){
        Map<Site,Integer> map = new HashMap<>();
        for (Site site:sites) {
            map.put(site,calculateTotalLoad(serverLoads,site));
        }
        return map;
    }

    private Map<Site,Server> findTheBestServerInEachSite(Client client,Map<Server,Integer> serverLoads, Map<Site,List<Server>> sites){
        Map<Site,Server> map = new HashMap<>();
        for (Site site:sites.keySet()) {
            Server theBestServerInSite;
            theBestServerInSite = (Server)selectWMCServerOrSite(client,(List)sites.get(site),(Map)serverLoads);
            map.put(site,theBestServerInSite);
        }
        return map;
    }


    private Server selectCostBasedserver(Client client, List<Server> serversHavingFile, Map<Server,Integer> serverShares,Map<Server,Integer> serverLoads) {
        //TODO: that guy's algorithm
        Server selectedServer;
        List<Server> finallyQualifiedServers ;
        Server connectedServer = (Server)client.getLink().getOtherEndPoint(client);
        List<Server> nearestServers= networkGraph.getNearerServers(algorithmData.Radius,serversHavingFile,client, rnd);
        if (!(nearestServers==null || nearestServers.size()==0) ){
            finallyQualifiedServers =createQualifiedList(serverShares, nearestServers,connectedServer);
            if (!(finallyQualifiedServers==null || finallyQualifiedServers.size()==0)){
                return selectByShares(serverShares, finallyQualifiedServers);
            }
        }
        selectedServer = networkGraph.getLeastLoadedServer(serversHavingFile,serverLoads);
        return selectedServer;
    }

    private List<Server> createQualifiedList(Map<Server, Integer> serverShares, List<Server> nearestServers, Server connectedServer) {
        List<Server> finallyQualifiedServers = new ArrayList<>();
        for (Server candidateServer:nearestServers) {
            int candidateLoad =serverShares.get(candidateServer);
            if (candidateLoad<0){
                finallyQualifiedServers.add(candidateServer);
            }
        }
        if (finallyQualifiedServers.size()==0 && nearestServers.contains(connectedServer)){
            finallyQualifiedServers.add(connectedServer);
        }
        return finallyQualifiedServers;
    }

    private Server selectByShares(Map<Server, Integer> serverShares, List<Server> finallyQualifiedServers) {
        Server selectedServer;
        List<Float> maxims = new ArrayList<>();
        setShares(finallyQualifiedServers, serverShares , maxims);
        selectedServer=randomlySelects(finallyQualifiedServers,maxims);
        if (selectedServer==null) throw new RuntimeException();
        return selectedServer;
    }

    private void setShares(List<Server> finalList, Map<Server, Integer> serverShares, List<Float> maxims) {
        int sum = 0;
        for (int i = 0; i < finalList.size() ; i++) {
            sum +=serverShares.get(finalList.get(i));
        }
        float cum = 0;
        for (int i = 0; i < finalList.size() ; i++) {
            cum+=((float)serverShares.get(finalList.get(i)))/sum;
            maxims.add(cum);
        }


    }

    private Server randomlySelects(List<Server> finalList, List<Float> maxims) {
        float rnd = new Random().nextFloat();
        for (int i = 0; i < maxims.size() ; i++) {
            if (rnd<maxims.get(i)) return finalList.get(i);
        }
        return finalList.get(finalList.size()-1);
    }

    private Server selectMCSserver(Client client, List<Server> preFilteredServers,Map<Server, Integer> serverLoads) {
        Server selectedServer;
        List<Server> nearestServers= networkGraph.getNearestServers(algorithmData.MCS_DELTA.intValue(),preFilteredServers,client, rnd);
        if (nearestServers==null || nearestServers.size()==0) throw new RuntimeException();
        selectedServer = networkGraph.getLeastLoadedServer(nearestServers,serverLoads);
        return selectedServer;
    }

    private HasLoadAndCost selectWMCServerOrSite(Client client, List<HasLoadAndCost> havingFile, Map<HasLoadAndCost, Integer> loads) {
        HasLoadAndCost selectedServer;
        selectedServer = networkGraph.getMostDesirableServerOrSite(havingFile, loads ,algorithmData.WMC_ALPHA.doubleValue(),client);
        return selectedServer;
    }

//    private Server selectWMCsite(Client client, List<Server> serversHavingFile, Map<Server, Integer> serverLoads) {
//        Server selectedServer;
//        Map<Site, >
//        for (Server server:serversHavingFile) {
//            server.getSite()
//        }
//
//
//        selectedServer = networkGraph.getMostDesirableServerOrSite(serversHavingFile, serverLoads ,algorithmData.WMC_ALPHA.doubleValue(),client);
//        return selectedServer;
//    }

    private Server selectHoneyBeeServer(Client client, List<Server> serversHavingFile, List<Site> sitesHavingFile,Map<Server, Integer> serverLoads) {
        Server selectedServer;
        double randomDouble = algorithmData.random.nextDouble();
        if (randomDouble<algorithmData.HONEY_BEE_SEARCH_PROBABILITY.doubleValue())
            selectedServer = serversHavingFile.get(new Random().nextInt(serversHavingFile.size()));
        else {
            selectedServer = selectWMCsiteThenServer(client, serversHavingFile, serverLoads, sitesHavingFile);
        }

        return selectedServer;


    }

    private Server selectHoneyBeeServer2(Client client, List<Server> serversHavingFile, Map<Site,List<Server>> sitesHavingFile,Map<Server, Integer> serverLoads) {
        Server selectedServer;
        double randomDouble = algorithmData.random.nextDouble();
        if (randomDouble<algorithmData.HONEY_BEE_SEARCH_PROBABILITY.doubleValue())
            selectedServer = serversHavingFile.get(new Random().nextInt(serversHavingFile.size()));
        else {
            selectedServer = selectWMCsiteThenServer2(client, serverLoads, sitesHavingFile);
        }

        return selectedServer;


    }


//            }
//            Server ser = (Server) selectWMCServerOrSite(client, (List)serversHavingFile, (Map)serverLoads);
//            if (!selectedServer.equals(ser)){
//                System.out.println();

    private Server selectPSSserver(Client client, List<Server> serversHavingFile, Map<Server, Integer> serverLoads) {
        Server selectedServer;
        double randomFloat = algorithmData.random.nextDouble();
        if (Double.compare(randomFloat,algorithmData.PSS_PROBABILITY.doubleValue())==-1){
//            Logger.printWithoutTime("*******PSS wants to find the nearest Server.");
            selectedServer = networkGraph.getNearestServers(1,serversHavingFile,client,rnd).get(0);
        }else{
//            Logger.printWithoutTime("*******PSS wants to find the least loaded Server.");
            selectedServer = networkGraph.getLeastLoadedServer(serversHavingFile, serverLoads);
        }
//        Logger.printWithoutTime("*******Servers Having File:");
//        for (Server serverHavingFile:serversHavingFile) {
//            Logger.printWithoutTime(serverHavingFile.toString()+" queueSize = "+ serverLoads.get(serverHavingFile) + " cost = " + serverHavingFile.getCommunicationCostTable().get(client));
//        }
//        Logger.printWithoutTime("******* PSS selected "+ selectedServer);
        return selectedServer;
    }
}
