package entities.logical;

import entities.physical.Client;
import entities.physical.NetworkGraph;
import entities.physical.ProjectRun;
import entities.physical.Server;
import entities.utilities.logger.Logger;

import java.util.Collections;
import java.util.List;


/**
 * Created by hd on 2018/4/2 AD.
 */
public class RedirectingAlgorithm {
//    static float queryDelay = 0;
    public static Server selectServerToRedirect(RedirectingAlgorithmType redirectingAlgorithmType, List<Server> serversHavingFile, Client client){
        Server selectedServer;
        Collections.shuffle(serversHavingFile);
//        queryDelay = 0;
        switch (redirectingAlgorithmType){
            case PSS:
                selectedServer = selectPSSserver(client, serversHavingFile);
                break;
            case WMC:
                selectedServer = selectWMCserver(client, serversHavingFile);
                break;
            case MCS:
                selectedServer = selectMCSserver(client, serversHavingFile);
                break;
            default:
                throw new RuntimeException("Redirecting Algorithm is not defined");
        }

        return selectedServer;
    }
    private static Server selectMCSserver(Client client, List<Server> serversHavingFile) {
        Server selectedServer;
        List<Server> nearestServers= NetworkGraph.networkGraph.getNearestServers(DefaultValues.MCS_DELTA,serversHavingFile,client, true);
        if (nearestServers==null || nearestServers.size()==0) throw new RuntimeException();
        selectedServer = NetworkGraph.networkGraph.getLeastLoadedServer(nearestServers);
        return selectedServer;
    }

    private static Server selectWMCserver(Client client, List<Server> serversHavingFile) {
        Server selectedServer;
        selectedServer = NetworkGraph.networkGraph.getMostDesirableServer(serversHavingFile, DefaultValues.WMC_ALPHA,client);
        return selectedServer;
    }

    private static Server selectPSSserver(Client client, List<Server> serversHavingFile) {
        Server selectedServer;
        float randomFloat = DefaultValues.random.nextFloat();
        if (randomFloat<DefaultValues.PSS_PROBABILITY){
            Logger.printWithoutTime("*******PSS wants to find the nearest Server.");
            selectedServer = NetworkGraph.networkGraph.getNearestServer(serversHavingFile,client);
        }else{
            Logger.printWithoutTime("*******PSS wants to find the least loaded Server.");
            selectedServer = NetworkGraph.networkGraph.getLeastLoadedServer(serversHavingFile);
        }
        Logger.printWithoutTime("*******Servers Having File:");
        for (Server serverHavingFile:serversHavingFile) {
            Logger.printWithoutTime(serverHavingFile.toString()+" queueSize = "+ serverHavingFile.getServerLoad() + " cost = " + serverHavingFile.getCommunicationCostTable().get(client));
        }
        Logger.printWithoutTime("******* PSS selected "+ selectedServer);
        return selectedServer;
    }
}
