package entities.logical;

import com.sun.istack.internal.Nullable;
import edu.uci.ics.jung.graph.util.EdgeType;
import entities.Statistics.Result;
import entities.physical.*;
import entities.utilities.Poisson;
import entities.utilities.ZipfGenerator;
import entities.utilities.logger.Logger;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by hd on 2018/4/21 AD.
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


    public UnitSimulation(USimId uSimId, Configuration configuration, Result result,String algorithm , String updateTypeString,@Nullable Number paramValue ,@Nullable Number honeyBeeProb, @Nullable Number periodicStep,String paramName, String secParamName, String terParamName) {
        this.configuration = configuration;
        this.result = result;
        this.uSimID = uSimId;
        loadParams(paramValue,honeyBeeProb, periodicStep,paramName, secParamName, terParamName);
        algorithmData.redirectingAlgorithmType = RedirectingAlgorithmType.valueOf(algorithm);
        algorithmData.updateType = UpdateType.valueOf(updateTypeString);


    }

    private void loadParams(@Nullable Number paramValue,@Nullable Number honeyBeeProb, @Nullable Number periodicStep, String paramName,String secParamName, String terParamName) {
        try {
            Field secParam = AlgorithmData.class.getField(secParamName);
            secParam.set(algorithmData, honeyBeeProb);
        }catch (Exception e) {


        }
        try {
            Field terParam = AlgorithmData.class.getField(terParamName);
            terParam.set(algorithmData, periodicStep);
        }catch (Exception e){


        }
        try {
            Field algParam = AlgorithmData.class.getField(paramName);
            algParam.set(algorithmData, paramValue);
        }catch (Exception e){


        }

    }



    //    private MatFileWriter produceMatFile(String simulationName, ArrayList<Number[]> xValues, ArrayList<Number[]> yValues, String[] seriesNames){
//        for (int i = 0; i < xValues.size() ; i++) {
//            int sizeOf
//            MLDouble seriesData = new MLDouble(seriesNames[i], new int[] {.length,2});
//            for (int j = 0; j <xValues[i].length ; j++) {
//            }
//        }
//        for (int i = 0; i < 100; i++) {
//            seriesData.set((double)i, );
//        }
//        MatFileWriter writer = new MatFileWriter();
//
//    }


//    private static void simulateMCS(int numberOfPoints,int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, int numberOfRequests, float bandwidth, float propagationDelay, int sizeOfFiles, int numberOfRuns, float lambdaInOutRatio, String path, PrintWriter result, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, Float[] costStats, Float[] delayStats) throws IOException {
//        double startTime = System.currentTimeMillis();
//        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.MCS;
//        result.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
//        for (int i = 0; i <numberOfPoints ; i++){
////                saeed = i;
//            System.out.println(i);
//            for (int j = 0; j < numberOfRuns ; j++) {
//                PrintWriter logger = null;
//                if (DefaultValues.LOGGER_ON) {
//                    new File(path+"/logs/run"+j).mkdir();
//                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i+1 + ".txt"));
//                    Logger.printWriter = logger;
//                }
//                DefaultValues.MCS_DELTA =  i+1;
//                simulate(numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, lambdaInOutRatio, costStatsForAllRuns, delayStatsForAllRuns, i, j, logger);
//
//            }
//            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
//            result.print(DefaultValues.MCS_DELTA);
//
//            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
//        }
//
//        double finishTime = System.currentTimeMillis();
//        result.println("Duration(min): " + (finishTime - startTime)/60000);
//
//        result.close();
//    }

    public Float[] simulate(Configuration configuration, Result result, PrintWriter logger) {
//        double a = System.currentTimeMillis();
        initSimulator(configuration );
//        double b = System.currentTimeMillis();
//        System.out.println("initializing simulator :"+ (b-a));

        generateRequests(configuration);

//        double c = System.currentTimeMillis();
//        System.out.println("generating Requests : "+(c-b));
//        System.out.println("handling Events");
        Brain brain = new Brain();
        brain.eventsQueue= eventsQueue;
        brain.handleEvents();

//        double d = System.currentTimeMillis();

//        System.out.println("Handle Events : "+(d-c));
//        System.out.println(EventsQueue.maximumTime);

//        EventsQueue.maximumTime=0;

//        System.out.println(NetworkGraph.networkGraph.c);
//        System.out.println(NetworkGraph.networkGraph.t);
//        System.out.println("gathering Stats");
        Float[] singleRunRes = result.gatherStats(clients);

        if(logger!=null) {
            logger.close();
        }
//        System.out.println();
//        System.out.println("done(s) = "+ (System.currentTimeMillis()-a)/1000);
//        System.out.println("% of Time in redirecting Algs= " + (RedirectingAlgorithm.totalTime/(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Server handle Events= " + (Server.totalTimeInServerHandleEvent /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Client handle Events= " + (Client.totalTimeINClientHandleEvent /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Link handle Events= " + (Link.totalTimeInLinkHandleEvent /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Brain= " + (Brain.totalTimeInBrain /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Request generation= " + (totalTimeInGenerateRequests /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in initaion= " + (initiationTime /(System.currentTimeMillis()-a))*100);
//        System.out.println("% of Time in make Load List Ideally= " + (Server.totalTimeInMakeLoadListIdeally /(System.currentTimeMillis()-a))*100);
//        System.out.println("% MaximumQueue= " + Server.maxQueue);
//        RedirectingAlgorithm.totalTime = 0;
//                Server.totalTimeInServerHandleEvent = 0;
//        Server.maxQueue = 0;
//                Link.totalTimeInLinkHandleEvent = 0;
//                Client.totalTimeINClientHandleEvent = 0;
//                Brain.totalTimeInBrain = 0;
//                totalTimeInGenerateRequests = 0;
//        Server.totalTimeInMakeLoadListIdeally = 0;
//

        return singleRunRes;

    }




//    public static double totalTimeInGenerateRequests = 0;
    private void generateRequests(Configuration configuration) {
//        double tempTime = System.currentTimeMillis();
        int reqFileId, requestingClientID;
        Random random = new Random();
        double timePerReq=  DefaultValues.SERVICE_TIME / configuration.numberOfServers / configuration.lambdaInOutRatio;
        double lambda = 1d/timePerReq;
        Poisson poisson = new Poisson(lambda,random);
        float reqTime = 0f;
        for (int  j= 0; j < configuration.numberOfRequests; j++) {

            requestingClientID = algorithmData.random.nextInt(configuration.numberOfServers);
            reqFileId = algorithmData.random.nextInt(configuration.numberOfFiles);

            reqTime = getInterarrivalTime( random, reqTime , lambda, poisson);
            eventsQueue.addEvent(
                    new Event(EventType.sendReq, clients.get(requestingClientID), reqTime, null, reqFileId)
            );
        }
//        totalTimeInGenerateRequests += System.currentTimeMillis()-tempTime;

    }

    private float getInterarrivalTime( Random random, float reqTime , double lambda , Poisson poisson) {
        if(!algorithmData.poissonArrivalsActivated) {
            reqTime += random.nextFloat() * 2 * (1d/lambda);
        }else
            reqTime+= poisson.getNextTime();
        return reqTime;
    }

    static double initiationTime;

    private void initSimulator(Configuration configuration) {
         double EnteringTime = System.currentTimeMillis();

        resetSimulatorSettings();

        createFiles(configuration.numberOfFiles, configuration.sizeOfFiles);


        initiateServesAndClients(configuration.numberOfServers, configuration.propagationDelay, configuration.bandwidth);
        initiateSites(configuration.numberOfServers, configuration.numberofSites);
        correctLinksWeights();

        Map<Integer, List<Server>> serversHavingFile = new HashMap<>();

        setServersHavingFile(configuration.numberOfServers, serversHavingFile);

        assignFileListsToServers(configuration.numberOfFiles,configuration. numberOfServers,configuration. numberOfFilesPerServer);

        createSimpleTopology(configuration.numberOfServers,configuration. propagationDelay,configuration. bandwidth);

        addLinksToGraph();

        fillServersHavingFile(serversHavingFile);
        setServerLoadLists(configuration.numberOfServers);
        networkGraph.buildRoutingTables();
        redirectingAlgorithm.networkGraph = networkGraph;
        initiationTime = System.currentTimeMillis() - EnteringTime;

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

    private void fillServersHavingFile(Map<Integer, List<Server>> serversHavingFile) {
        StringBuffer sb = new StringBuffer();
        List<Server> serversss ;
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

    private void createSimpleTopology(int numberOfServers, float propDelay, float bw) {
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

    private void assignFileListsToServers(int numberOfFiles, int numberOfServers, int numberOfFilesPerServer) {
        int[][] serverContents = ZipfGenerator.returnFileList(1,numberOfFilesPerServer,numberOfFiles,numberOfServers);
        for (int i = 0; i < numberOfServers ; i++) {
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
//            servers.get(i).setShares();
        }
    }
    private void resetSimulatorSettings() {
//        EventsQueue.lastSentPeriod =0;
//        NetworkGraph.renewNetworrkGraph();
//         networkGraph = new NetworkGraph();
//        networkGraph = NetworkGraph.networkGraph;
//        eventsQueue = new EventsQueue(this);
//        RedirectingAlgorithm.rnd = new Random() ;
//        algorithmData.random = new Random() ;
//        System.gc();
    }

    @Override
    public Float[] call() {
        System.out.println(uSimID.simulationName+"  "+uSimID.ithPoint+"  "+"Run : "+uSimID.jthRun);
        return simulate(configuration, result, null);

    }
}
