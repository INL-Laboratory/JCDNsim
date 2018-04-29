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
//        for (int i = 0; i < serversHavingFile.size(); i++) {
//            if (!serversHavingFile.contains(nearestServers.get(i))){
//                System.out.println("klm");
//            }
//        }
        if (nearestServers==null || nearestServers.size()==0) throw new RuntimeException();
//        if (nearestServers.size()==4 && nearestServers.get(0).getServerLoad()>5){
//            System.out.println();
//        }
        selectedServer = NetworkGraph.networkGraph.getLeastLoadedServer(nearestServers);
//        Server selectedServer1 = null;
//        Server selectedServer2 = null;
//        if (saeed == 0 ) {
//            DefaultValues.WMC_ALPHA = 1;
//            DefaultValues.PSS_PROBABILITY = 1;
//        }
//        if (saeed == 16 ) {
//            DefaultValues.WMC_ALPHA = 0;
//            DefaultValues.PSS_PROBABILITY = 0;
//        }
////            System.out.println("PSS:" + selectedServer1);
////            System.out.println("WMC:" + selectedServer2);
////            System.out.println("MCS:" + selectedServer);
//        if (saeed == 16 || saeed ==0) {
//            selectedServer1 = selectPSSserver(client, serversHavingFile);
//            selectedServer2 = selectWMCserver(client, serversHavingFile);
//            if (!selectedServer1.equals(selectedServer)) {
//                System.out.println("Error");
//            }
//        }
        return selectedServer;
    }

    private static Server selectWMCserver(Client client, List<Server> serversHavingFile) {
        Server selectedServer;
        selectedServer = NetworkGraph.networkGraph.getMostDesirableServer(serversHavingFile, DefaultValues.WMC_ALPHA,client);
//        Server selectedServer2 = selectPSSserver(client, serversHavingFile);
//        if (!selectedServer.equals(selectedServer2)) {
//            System.out.println();
//            System.out.println("sdf");
//        }

        return selectedServer;
//        Server selectedServer;
//        float randomFloat = DefaultValues.random.nextFloat();
////        if (saeed == 0 ) {
////            DefaultValues.WMC_ALPHA = 0;
////        }
////        if (saeed == 1f ) {
////            DefaultValues.WMC_ALPHA = 1;
////        }
//        DefaultValues.WMC_ALPHA = DefaultValues.PSS_PROBABILITY;
////        Server selectedServer2 = selectWMCserver(client, serversHavingFile);
//
//
//        if (randomFloat<DefaultValues.PSS_PROBABILITY){
//            Logger.printWithoutTime("*******PSS wants to find the nearest Server.");
//            selectedServer = NetworkGraph.networkGraph.getNearestServer(serversHavingFile,client);
//        }else{
//            Logger.printWithoutTime("*******PSS wants to find the least loaded Server.");
//            selectedServer = NetworkGraph.networkGraph.getLeastLoadedServer(serversHavingFile);
//        }
//        Logger.printWithoutTime("*******Servers Having File:");
//        for (Server serverHavingFile:serversHavingFile) {
//            Logger.printWithoutTime(serverHavingFile.toString()+" queueSize = "+ serverHavingFile.getServerLoad() + " cost = " + serverHavingFile.getCommunicationCostTable().get(client));
//        }
//        Logger.printWithoutTime("******* PSS selected "+ selectedServer);
////        if (saeed==0||saeed==10 )
////        if (!selectedServer .equals(selectedServer2))
////            System.out.println("Error");
//        return selectedServer;

    }

    private static Server selectPSSserver(Client client, List<Server> serversHavingFile) {
        Server selectedServer;
        float randomFloat = DefaultValues.random.nextFloat();
//        if (saeed == 0 ) {
//            DefaultValues.WMC_ALPHA = 0;
//        }
//        if (saeed == 1f ) {
//            DefaultValues.WMC_ALPHA = 1;
//        }


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
//        if (saeed==0||saeed==10 )
        return selectedServer;
    }
}
