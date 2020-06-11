/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

import edu.uci.ics.jung.graph.util.EdgeType;
import entities.Algortihms.RedirectingAlgorithm;
import entities.Setting.*;
import entities.Statistics.Result;
import entities.Network.*;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;


/**Created by hd on 2018/4/21 AD.
    UnitSimulation is a simulation of a single replication at a single point.
 */
public class UnitSimulation implements Callable<Float[]> {
    private List<Server> servers = new ArrayList<>();
    private List<Site> sites = new ArrayList<>();
    private List<Client>  clients= new ArrayList<>();
    private List<IFile>  files= new ArrayList<>();
    private List<Link>  links= new ArrayList<>();
    private NetworkGraph networkGraph = new NetworkGraph();
    private EventsQueue eventsQueue = new EventsQueue(this);
    private final Configuration configuration;
    private final Result result;
    public final USimId uSimID;
    final AlgorithmData algorithmData = new AlgorithmData();
    private final RedirectingAlgorithm redirectingAlgorithm = new RedirectingAlgorithm(algorithmData);
    private final List<RequestEvent> requestEvents;
    private final int[][] serverContents;
    private final Topology topology ;
    public UnitSimulation(USimId uSimId, RunningParameters runParams, Result result, Number vParam, List<RequestEvent> requestEvents,int[][] servercontents, Topology topology) {
        this.configuration = runParams.configuration;
        this.result = result;
        this.uSimID = uSimId;
        loadParams(vParam,runParams.variableParam,runParams.fixedParamsBundle);
        this.algorithmData.redirectingAlgorithmType = RedirectingAlgorithmType.valueOf(runParams.algorithm);
        this.algorithmData.updateType = UpdateType.valueOf(runParams.updateType);
        this.requestEvents = requestEvents;
        this.serverContents= servercontents;
        this.topology = topology;

    }

    private void loadParams(Number vParam, String vParamName, Map<String,Number> fParamsBundle) {

        try {
            Field param = AlgorithmData.class.getField(vParamName);
            param.set(algorithmData, vParam);
        }catch (Exception e) {}

        if (fParamsBundle==null) return;
        for (String fParamName:fParamsBundle.keySet()) {
            try {
                Field fparam = AlgorithmData.class.getField(fParamName);
                fparam.set(algorithmData, fParamsBundle.get(fParamName));
            }catch (Exception e) {}

        }

    }



    private Float[] simulate(Configuration configuration, Result result, PrintWriter logger) {
        initSimulator(configuration );
        generateRequests();

        Brain brain = new Brain();
        brain.eventsQueue= eventsQueue;
        brain.handleEvents();

        Float[] singleRunRes = result.gatherStats(clients);

        if(logger!=null) {
            logger.close();
        }

        return singleRunRes;

    }




    private void generateRequests() {

        for (RequestEvent rq:requestEvents) {
            eventsQueue.addEvent(
                    new Event<>(EventType.sendReq, clients.get(rq.requestingClientID), rq.reqTime, null, rq.requestedFileID)
            );

        }


    }


    private void initSimulator(Configuration configuration) {
        createFiles(configuration.numberOfFiles, configuration.sizeOfFiles);


        initiateServesAndClients(configuration.numberOfServers, configuration.propagationDelay, configuration.bandwidth);
        initiateSites(configuration.numberOfServers, configuration.numberofSites);
        correctLinksWeights();

        Map<Integer, List<Server>> serversHavingFile = new HashMap<>();
        Map<Integer, Map<Site,List<Server>>> sitesHavingFile = new HashMap<>();

        setServersHavingFile(configuration.numberOfServers, serversHavingFile);
        setSitesHavingFile(configuration.numberOfServers, sitesHavingFile);

        assignFileListsToServers(serverContents);

        createTopology(configuration. propagationDelay,configuration. bandwidth,topology);

        addLinksToGraph();

        fillSitesAndServersHavingFile(serversHavingFile, sitesHavingFile);
        setServerLoadLists(configuration.numberOfServers);
        networkGraph.buildRoutingTables();
        for (Site site:sites) {
            site.makeRoutingTable();
        }
        redirectingAlgorithm.networkGraph = networkGraph;

    }

    private void setSitesHavingFile(int numberOfServers, Map<Integer,Map<Site,List<Server>>> sitesHavingFile) {
        for (int i = 0; i < numberOfServers ; i++) {
            servers.get(i).setSitesHavingFile(sitesHavingFile);
        }
    }

    private void correctLinksWeights() {
        for (Link link:links) {
            EndDevice endDevice1 = link.getEndPointA();
            EndDevice endDevice2 = link.getEndPointB();
            if (endDevice1 instanceof Server && endDevice2 instanceof Server){
                if (((Server) endDevice1).getSite().equals(((Server) endDevice2).getSite()))
                    link.setWeight(DefaultValues.SERVER_SERVER_LOCAL_WEIGHT);
                else
                    link.setWeight(DefaultValues.SERVER_SERVER_INTERSITE_WEIGHT);
            }else link.setWeight(DefaultValues.SERVER_CLIENT_WEIGHT);
        }
    }

    private void initiateSites(int numberOfServers, int numberofSites) {
        ArrayList<Server> serverArrayList = new ArrayList<>(servers.size());

        for (int i = 0; i < numberOfServers; i++) {
            serverArrayList.add(servers.get(i));
        }

        for (int i = 0; i < numberofSites ; i++) {
            if (serverArrayList.size()==0) break;
            Site site = new Site(i);
            sites.add(site);
            for (int j = 0; j < numberOfServers / numberofSites ; j++) {
                if (serverArrayList.size()==0) break;
                site.addServer(serverArrayList.remove(0));
            }
        }

    }

    private void fillSitesAndServersHavingFile(Map<Integer, List<Server>> serversHavingFile,Map<Integer, Map<Site,List<Server>>> sitesHavingFile) {
        StringBuffer sb = new StringBuffer();
        List<Server> serversss ;
        Map<Site,List<Server>> sites ;
//        sb.append(" ***** Files ***** ");
//
        for(IFile f: files){
            serversss = networkGraph.getServersHavingFile(f.getId());
            serversHavingFile.put(f.getId(),serversss);


//            sb.append("\n").append(f).append(" :");
//            for(Server s: serversss){
//                sb.append("  ").append(s);
//            }
        }
        for(IFile f: files){
            sites = networkGraph.getSitesHavingFile(f.getId());
            sitesHavingFile.put(f.getId(),sites);


//            sb.append("\n").append(f).append(" :");
//            for(Server s: serversss){
//                sb.append("  ").append(s);
//            }
        }
//        Logger.print(sb.toString(),0);
    }

    private void addLinksToGraph() {
        for (Server s:servers){
            networkGraph.addVertex(s);
//            try {
//                if (s.getLinks().size() != 7) throw new RuntimeException();
//            }catch (Exception e){
//                System.out.println();
//            }

        }
        for (Client c:clients){
            networkGraph.addVertex(c);
        }
        for (Link l:links){
            networkGraph.addEdge(l,l.getEndPointA(),l.getEndPointB(), EdgeType.UNDIRECTED);
        }
    }

    private void createDefaultTopology(int numberOfServers, float propDelay, float bw) {
        int targetServerId;
        for (int i = 0; i < numberOfServers; i++) {
            Server server =  servers.get(i);
            for (int j = 0; j < 3 ; j++) {
                targetServerId = i + 1 + j;
                if (targetServerId>=numberOfServers){
                    targetServerId -= numberOfServers;
                }
                if (targetServerId== i){
                    targetServerId++;
                }
                Server targetServer = servers.get(targetServerId);
                Link link = new Link(server,targetServer,propDelay,bw,1, eventsQueue);
                targetServer.getLinks().put(server,link);
                server.getLinks().put(targetServer,link);
                links.add(link);
            }
        }
    }
    public final static int DEFAULT_WEIGHT = 1;
    private void createTopology(float propDelay, float bw, Topology topology) {
        boolean[][] adjacencyMatrix = topology.adjMat;
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[0].length ; j++) {
                if (adjacencyMatrix[i][j]) {
                    Server server1 = servers.get(i);
                    Server server2 = servers.get(j);
                    if (server1.getLinks().get(server2)!=null) continue;
                    int weight = topology.weight==null?DEFAULT_WEIGHT:topology.weight[i][j];
                    Link link = new Link(server1, server2, propDelay, bw, weight, eventsQueue);
                    server1.getLinks().put(server2, link);
                    server2.getLinks().put(server1, link);
                    links.add(link);
                }
            }
        }


    }


    private void initiateServesAndClients(int numberOfServers, float propDelay, float bw) {
        for (int i = 0; i < numberOfServers ; i++) {

            Server server = new Server(i,eventsQueue, algorithmData,redirectingAlgorithm);
            servers.add(server);
            Client client = new Client(-i, eventsQueue,algorithmData);
            clients.add(client);
            Link link = new Link(client,server,propDelay,bw,1, eventsQueue);
            links.add(link);
            client.setLink(link);
            server.getLinks().put(client,link);
        }
    }

    private void setServersHavingFile(int numberOfServers, Map<Integer, List<Server>> serversHavingFile) {
        for (int i = 0; i < numberOfServers ; i++) {
            servers.get(i).setServersHavingFile(serversHavingFile);
        }
    }
    private void setServerLoadLists(int numberOfServers) {
        for (int i = 0; i < numberOfServers ; i++) {
            for (int j = 0; j < numberOfServers; j++) {
                servers.get(i).getServerLoadListss().put(servers.get(j),0);
            }
        }
    }

    private void assignFileListsToServers(int[][] serverContents) {
//        int[][] serverContents = ZipfGenerator.returnFileList(1,numberOfFilesPerServer,numberOfFiles,numberOfServers);
        for (int i = 0; i < servers.size() ; i++) {
            List<IFile> fileList = new LinkedList<>();
            for (int j = 0; j < serverContents[i].length ; j++) {
                fileList.add(files.get(serverContents[i][j]));
            }
            servers.get(i).setFiles(fileList);
        }
    }

    private void createFiles(int numberOfFiles, float sizeOfFiles) {
        for (int i = 0; i < numberOfFiles; i++) {
            files.add(new IFile(i,sizeOfFiles));
        }
    }

    public void sendPeriodicUpdate(float time, boolean groupedPeriodic) {
//        System.out.println(time + " periodic updates are being sent");
        for (int i = 0; i < servers.size(); i++) {
                if (!groupedPeriodic) servers.get(i).sendUpdateToAll(time,servers);
                else {
                    try {
                        servers.get(i).sendUpdateToAll(time, servers.get(i).getSite().getServers());
                    }catch (Exception e){
                        System.out.println("d");
                    }
                }
        }
    }
    public void setShares() {
        for (int i = 0; i < servers.size(); i++) {
            servers.get(i).setShares();
        }
    }

    @Override
    public Float[] call() {
        System.out.println(uSimID.simulationName+"  variable: "+uSimID.ithPoint+"  "+"Run : "+uSimID.jthRun);
        return simulate(configuration, result, null);

    }
}
