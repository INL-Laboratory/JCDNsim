package entities.logical;

import entities.physical.Client;
import entities.physical.NetworkGraph;
import entities.physical.Server;

import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by hd on 2018/4/2 AD.
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

    public Server selectServerToRedirect(RedirectingAlgorithmType redirectingAlgorithmType, List<Server> serversHavingFile , Map<Server, Integer> serverLoads , Client client){
//        double a = System.currentTimeMillis();

        Server selectedServer;
//        queryDelay = 0;
        switch (redirectingAlgorithmType){
            case PSS:
                selectedServer = selectPSSserver(client, serversHavingFile,serverLoads);
                break;
            case WMC:
                selectedServer = selectWMCserver(client, serversHavingFile,serverLoads);
                break;
            case MCS:
                selectedServer = selectMCSserver(client, serversHavingFile,serverLoads);
                break;
            case HONEYBEE:
                selectedServer = selectHoneyBeeServer(client, serversHavingFile,serverLoads);
                break;
            default:
                throw new RuntimeException("Redirecting Algorithm is not defined");
        }

//        totalTime += System.currentTimeMillis()-a;
        return selectedServer;
    }
    private Server selectMCSserver(Client client, List<Server> serversHavingFile,Map<Server, Integer> serverLoads) {
        Server selectedServer;
        List<Server> nearestServers= networkGraph.getNearestServers(algorithmData.MCS_DELTA.intValue(),serversHavingFile,client, rnd);
        if (nearestServers==null || nearestServers.size()==0) throw new RuntimeException();
        selectedServer = networkGraph.getLeastLoadedServer(nearestServers,serverLoads);
        return selectedServer;
    }

    private Server selectWMCserver(Client client, List<Server> serversHavingFile, Map<Server, Integer> serverLoads) {
        Server selectedServer;
        selectedServer = networkGraph.getMostDesirableServer(serversHavingFile, serverLoads ,algorithmData.WMC_ALPHA.doubleValue(),client);
        return selectedServer;
    }

    private Server selectHoneyBeeServer(Client client, List<Server> serversHavingFile, Map<Server, Integer> serverLoads) {
        Server selectedServer;
        double randomDouble = algorithmData.random.nextDouble();
        if (randomDouble<algorithmData.HONEY_BEE_SEARCH_PROBABILITY.doubleValue())
            selectedServer = serversHavingFile.get(new Random().nextInt(serversHavingFile.size()));
            else
            selectedServer = selectWMCserver(client,serversHavingFile,serverLoads);
        return selectedServer;


    }


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
