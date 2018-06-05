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
    static float queryDelay = 0;
    public static Random rnd = new Random();
//    public static double totalTime = 0;
    public static Server selectServerToRedirect( RedirectingAlgorithmType redirectingAlgorithmType, List<Server> serversHavingFile , Map<Server, Integer> serverLoads ,Client client){
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
    private static Server selectMCSserver(Client client, List<Server> serversHavingFile,Map<Server, Integer> serverLoads) {
        Server selectedServer;
        List<Server> nearestServers= NetworkGraph.networkGraph.getNearestServers(DefaultValues.MCS_DELTA.intValue(),serversHavingFile,client, rnd);
        if (nearestServers==null || nearestServers.size()==0) throw new RuntimeException();
        selectedServer = NetworkGraph.networkGraph.getLeastLoadedServer(nearestServers,serverLoads);
        return selectedServer;
    }

    private static Server selectWMCserver(Client client, List<Server> serversHavingFile, Map<Server, Integer> serverLoads) {
        Server selectedServer;
        selectedServer = NetworkGraph.networkGraph.getMostDesirableServer(serversHavingFile, serverLoads ,DefaultValues.WMC_ALPHA.doubleValue(),client);
        return selectedServer;
    }

    private static Server selectHoneyBeeServer(Client client, List<Server> serversHavingFile, Map<Server, Integer> serverLoads) {
        Server selectedServer;
        double randomDouble = DefaultValues.random.nextDouble();
        if (randomDouble<DefaultValues.HONEY_BEE_SEARCH_PROBABILITY.doubleValue())
            selectedServer = serversHavingFile.get(new Random().nextInt(serversHavingFile.size()));
            else
            selectedServer = selectWMCserver(client,serversHavingFile,serverLoads);
        return selectedServer;


    }


    private static Server selectPSSserver(Client client, List<Server> serversHavingFile, Map<Server, Integer> serverLoads) {
        Server selectedServer;
        double randomFloat = DefaultValues.random.nextDouble();
        if (Double.compare(randomFloat,DefaultValues.PSS_PROBABILITY.doubleValue())==-1){
//            Logger.printWithoutTime("*******PSS wants to find the nearest Server.");
            selectedServer = NetworkGraph.networkGraph.getNearestServers(1,serversHavingFile,client,rnd).get(0);
        }else{
//            Logger.printWithoutTime("*******PSS wants to find the least loaded Server.");
            selectedServer = NetworkGraph.networkGraph.getLeastLoadedServer(serversHavingFile, serverLoads);
        }
//        Logger.printWithoutTime("*******Servers Having File:");
//        for (Server serverHavingFile:serversHavingFile) {
//            Logger.printWithoutTime(serverHavingFile.toString()+" queueSize = "+ serverLoads.get(serverHavingFile) + " cost = " + serverHavingFile.getCommunicationCostTable().get(client));
//        }
//        Logger.printWithoutTime("******* PSS selected "+ selectedServer);
        return selectedServer;
    }
}
