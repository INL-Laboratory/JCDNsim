package entities.physical;

import edu.uci.ics.jung.graph.util.EdgeType;
import entities.logical.*;
import entities.utilities.Chart;
import entities.utilities.logger.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by hd on 2018/4/21 AD.
 */
public class ProjectRun {
    static List<Server> servers = new ArrayList<>();
    static List<Client>  clients= new ArrayList<>();
    static List<IFile>  files= new ArrayList<>();
    static List<Link>  links= new ArrayList<>();
    static NetworkGraph networkGraph = NetworkGraph.networkGraph;



    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss");
    private static LocalDateTime now = LocalDateTime.now();


    public static void main(String[] args) throws IOException {
        final int numberOfFiles = 35;
        final int numberOfServers = 35;
        final int numberOfFilesPerServer = 15;
        final int numberOfRequests =500000;
        final float bandwidth = 10000000f;
        final float propagationDelay = 0f;
        final int sizeOfFiles = 50;
        final int numberOfRuns = 100;
        final float lambdaInOutRatio = 0.999f;
        String path = "results";
        new File( path).mkdir();
        path = "results/"+dtf.format(now);
        new File( path).mkdir();
        new File(path+"/logs").mkdir();
        new File(path+"/chart").mkdir();
        PrintWriter parametersFile = new PrintWriter(new FileWriter(path+"/parameters.txt"));
        PrintWriter result0 = new PrintWriter(new FileWriter(path+"/resultPSS.txt"));
        PrintWriter result1 = new PrintWriter(new FileWriter(path+"/resultWMC.txt"));
        PrintWriter result2 = new PrintWriter(new FileWriter(path+"/resultMCS.txt"));
        parametersFile.println("# of Servers: " + numberOfServers);
        parametersFile.println("# of Run for each point: " + numberOfRuns);
        parametersFile.println("# of Requests: " + numberOfRequests);
        parametersFile.println("# of Files: " + numberOfFiles);
        parametersFile.println("# of Files per Server: " + numberOfFilesPerServer);
        parametersFile.println("Size of Files(KB): " + sizeOfFiles);
        parametersFile.println("Request Size(KB) : " + DefaultValues.REQUEST_SIZE);
        parametersFile.println("Service Time(ms) " + DefaultValues.SERVICE_TIME);
        parametersFile.println("Request generation average interval(ms): float[0,1]*2* " + DefaultValues.SERVICE_TIME/numberOfServers/lambdaInOutRatio);
        parametersFile.println("lambdaInPerOutRatio: " + lambdaInOutRatio);
        parametersFile.println("Propagation Delay(ms):" + propagationDelay);
        parametersFile.println("BandWidth(KB/ms):" + bandwidth);
        parametersFile.println("Time out(ms):  " + (DefaultValues.IS_TIME_OUT_ACTIVATED?"Enabled":"Disabled"));
        parametersFile.println((DefaultValues.IS_TIME_OUT_ACTIVATED?"Time out time: " + DefaultValues.TIME_OUT:""));
        parametersFile.close();



//
        int numberOfPoints = 11;
//        Float[] costStats0 = new Float[numberOfPoints];
//        Float[] delayStats0 = new Float[numberOfPoints];
//        Float[][] costStatsForAllRuns0 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] delayStatsForAllRuns0 = new Float[numberOfPoints][numberOfRuns];
//        simulatePSS(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result0, costStatsForAllRuns0, delayStatsForAllRuns0, costStats0, delayStats0);
//
//        numberOfPoints = 11;
//        Float[] costStats1 = new Float[numberOfPoints];
//        Float[] delayStats1 = new Float[numberOfPoints];
//        Float[][] delayStatsForAllRuns1 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] costStatsForAllRuns1 = new Float[numberOfPoints][numberOfRuns];
//        simulateWMC(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result1, costStatsForAllRuns1, delayStatsForAllRuns1, costStats1, delayStats1);

         numberOfPoints = 16;

        Float[] costStats2 = new Float[numberOfPoints];
        Float[] delayStats2 = new Float[numberOfPoints];
        Float[][] costStatsForAllRuns2 = new Float[numberOfPoints][numberOfRuns];
        Float[][] delayStatsForAllRuns2 = new Float[numberOfPoints][numberOfRuns];
        simulateMCS(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result2, costStatsForAllRuns2, delayStatsForAllRuns2, costStats2, delayStats2);




        //Chart stuff
        String pathName = path+"/chart/photo.png" ,
                seriesName0 = "PSS",
                seriesName1 = "WMC",
                seriesName2 = "MCS";
        Chart.initiateChart(pathName);
//        Chart.addSeries(seriesName0, costStats0, delayStats0);
//        Chart.addSeries(seriesName1, costStats1, delayStats1);
        Chart.addSeries(seriesName2, costStats2, delayStats2);
        Chart.main(args);

    }
    private static void simulatePSS(int numberOfPoints,int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, int numberOfRequests, float bandwidth, float propagationDelay, int sizeOfFiles, int numberOfRuns, float lambdaInOutRatio, String path, PrintWriter result, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, Float[] costStats, Float[] delayStats) throws IOException {
        double startTime = System.currentTimeMillis();
        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.PSS;
        result.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
        DefaultValues.PSS_PROBABILITY = 0;
        for (int i = 0; i < numberOfPoints; i++){
            for (int j = 0; j < numberOfRuns ; j++) {
                PrintWriter logger = null;
                if (DefaultValues.LOGGER_ON) {
                    new File(path+"/logs/run"+j).mkdir();
                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
                    Logger.printWriter = logger;
                }
                DefaultValues.PSS_PROBABILITY = 0.1f*i;
                System.out.println(DefaultValues.PSS_PROBABILITY+"\t" + j);
                initSimulator(numberOfFiles, numberOfServers, numberOfFilesPerServer , propagationDelay , bandwidth,sizeOfFiles );
                generateRequests(numberOfRequests,numberOfFiles, numberOfServers , lambdaInOutRatio);
                Brain.handleEvents();
                gatherStats(costStatsForAllRuns, delayStatsForAllRuns, i,j);
                if(logger!=null) {
                    logger.close();
                }
            }
            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
            result.print(DefaultValues.PSS_PROBABILITY);
            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
        }

        double finishTime = System.currentTimeMillis();
        result.println("Duration(min): " + (finishTime - startTime)/60000);

        result.close();
    }


    private static void simulateWMC(int numberOfPoints,int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, int numberOfRequests, float bandwidth, float propagationDelay, int sizeOfFiles, int numberOfRuns, float lambdaInOutRatio, String path, PrintWriter result, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, Float[] costStats, Float[] delayStats) throws IOException {
        double startTime = System.currentTimeMillis();
        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.WMC;
        result.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
        DefaultValues.WMC_ALPHA = 0;
        for (int i = 0; i < numberOfPoints; i++){
            for (int j = 0; j < numberOfRuns ; j++) {
                PrintWriter logger = null;
                if (DefaultValues.LOGGER_ON) {
                    new File(path+"/logs/run"+j).mkdir();
                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
                    Logger.printWriter = logger;
                }
                DefaultValues.WMC_ALPHA = 0.1f*i;
                System.out.println(DefaultValues.WMC_ALPHA+"\t" + j);
                initSimulator(numberOfFiles, numberOfServers, numberOfFilesPerServer , propagationDelay , bandwidth,sizeOfFiles );
                generateRequests(numberOfRequests,numberOfFiles, numberOfServers , lambdaInOutRatio);
                Brain.handleEvents();
                gatherStats(costStatsForAllRuns, delayStatsForAllRuns, i,j);
                if(logger!=null) {
                    logger.close();
                }
            }
            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
            result.print(DefaultValues.WMC_ALPHA);
            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
        }

        double finishTime = System.currentTimeMillis();
        result.println("Duration(min): " + (finishTime - startTime)/60000);

        result.close();
    }

    private static void simulateMCS(int numberOfPoints,int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, int numberOfRequests, float bandwidth, float propagationDelay, int sizeOfFiles, int numberOfRuns, float lambdaInOutRatio, String path, PrintWriter result, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, Float[] costStats, Float[] delayStats) throws IOException {
        double startTime = System.currentTimeMillis();
        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.MCS;
        result.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
        for (int i = 0; i <numberOfPoints ; i++){
//                saeed = i;
            for (int j = 0; j < numberOfRuns ; j++) {
                double a = System.currentTimeMillis();
                PrintWriter logger = null;
                if (DefaultValues.LOGGER_ON) {
                    new File(path+"/logs/run"+j).mkdir();
                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i+1 + ".txt"));
                    Logger.printWriter = logger;
                }
                DefaultValues.MCS_DELTA =  i+1;
                System.out.println(DefaultValues.MCS_DELTA+"\t" + j);
                System.out.println("initializing simulator");
                initSimulator(numberOfFiles, numberOfServers, numberOfFilesPerServer , propagationDelay , bandwidth,sizeOfFiles );
                System.out.println("generating Requests");
                generateRequests(numberOfRequests,numberOfFiles, numberOfServers , lambdaInOutRatio);
                System.out.println("handling Events");
                Brain.handleEvents();
                System.out.println(NetworkGraph.networkGraph.c);
                System.out.println(NetworkGraph.networkGraph.t);
                System.out.println("gathering Stats");
                gatherStats(costStatsForAllRuns, delayStatsForAllRuns, i,j);
                if(logger!=null) {
                    logger.close();
                }
                System.out.println();
                System.out.println("done(s) = "+ (System.currentTimeMillis()-a)/1000);
//                System.out.println("% of Time in redirecting Algs= " + (RedirectingAlgorithm.totalTime/(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Server handle Events= " + (Server.totalTimeInServerHandleEvent /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Client handle Events= " + (Client.totalTimeINClientHandleEvent /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Link handle Events= " + (Link.totalTimeInLinkHandleEvent /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Brain= " + (Brain.totalTimeInBrain /(System.currentTimeMillis()-a))*100);
//                System.out.println("% of Time in Request generation= " + (totalTimeInGenerateRequests /(System.currentTimeMillis()-a))*100);
                System.out.println("% MaximumQueue= " + Server.maxQueue);
//                RedirectingAlgorithm.totalTime = 0;
//                Server.totalTimeInServerHandleEvent = 0;
//                Server.maxQueue = 0;
//                Link.totalTimeInLinkHandleEvent = 0;
//                Client.totalTimeINClientHandleEvent = 0;
//                Brain.totalTimeInBrain = 0;
//                totalTimeInGenerateRequests = 0;
//

            }
            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
            result.print(DefaultValues.MCS_DELTA);

            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
        }

        double finishTime = System.currentTimeMillis();
        result.println("Duration(min): " + (finishTime - startTime)/60000);

        result.close();
    }


    private static void calcAverageOnAllRuns(Float[] costStats, Float[] delayStats, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, int i) {
        float costSum = 0 , delaySum = 0;
        int numberOfRuns = costStatsForAllRuns[0].length;
            for (int j = 0; j < numberOfRuns; j++) {
                costSum+=costStatsForAllRuns[i][j];
                delaySum+=delayStatsForAllRuns[i][j];
            }
            costStats[i] = costSum/numberOfRuns;
            delayStats[i] = delaySum/numberOfRuns;
    }


    private static void gatherStats(Float[][] costStats, Float[][] delayStats, int i,int j) {
        int totalCost = 0;
        float counter = 0;
        float totalDelay = 0f;
        for (Client client:clients) {
            for (int reqID:client.getServedRequestsCost().keySet()){
                float sendTime = client.getSentRequestsTime().get(reqID);
                float servedTime = client.getServedRequestsTime().get(reqID);
                totalDelay +=servedTime-sendTime;
                int cost = client.getServedRequestsCost().get(reqID);
                totalCost+=cost;
                counter++;
            }
        }

        costStats[i][j] = totalCost/counter;
        delayStats[i][j]= totalDelay/counter;
    }
    public static double totalTimeInGenerateRequests = 0;
    private static void generateRequests(int numberOfRequests, int numberOfFiles, int numberOfServers, float ratio) {
//        double tempTime = System.currentTimeMillis();
        int reqFileId, requestingClientID;
        Random random = new Random();
        float reqTime = 0f;
        for (int  j= 0; j < numberOfRequests; j++) {

            requestingClientID = DefaultValues.random.nextInt(numberOfServers);
            reqFileId = DefaultValues.random.nextInt(numberOfFiles);

            reqTime += random.nextFloat()*2*DefaultValues.SERVICE_TIME/numberOfServers/ratio;
            EventsQueue.addEvent(
                    new Event(EventType.sendReq, clients.get(requestingClientID), reqTime, null, reqFileId)
            );
        }
//        totalTimeInGenerateRequests += System.currentTimeMillis()-tempTime;

    }

    private static void initSimulator(int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, float propDelay, float bw, int sizeOfFiles) {
        resetSimulatorSettings();

        createFiles(numberOfFiles, sizeOfFiles);


        initiateServesAndClients(numberOfServers, propDelay, bw);

        Map<Integer, List<Server>> serversHavingFile = new HashMap<>();

        setServersHavingFile(numberOfServers, serversHavingFile);

        assignFileListsToServers(numberOfFiles, numberOfServers, numberOfFilesPerServer);

        createSimpleTopology(numberOfServers, propDelay, bw);

        addLinksToGraph();

        fillServersHavingFile(serversHavingFile);
        networkGraph.buildRoutingTables();
    }

    private static void fillServersHavingFile(Map<Integer, List<Server>> serversHavingFile) {
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

    private static void addLinksToGraph() {
        for (Server s:servers){
            networkGraph.addVertex(s);
            try {
                if (s.getLinks().size() != 7) throw new RuntimeException();
            }catch (Exception e){
                System.out.println();
            }

        }
        for (Client c:clients){
            networkGraph.addVertex(c);
        }
        for (Link l:links){
            networkGraph.addEdge(l,l.getEndPointA(),l.getEndPointB(), EdgeType.UNDIRECTED);
        }
    }

    private static void createSimpleTopology(int numberOfServers, float propDelay, float bw) {
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
                Link link = new Link(server,targetServer,propDelay,bw,1);
                targetServer.getLinks().put(server,link);
                server.getLinks().put(targetServer,link);
                links.add(link);
            }
        }
    }

    private static void initiateServesAndClients(int numberOfServers, float propDelay, float bw) {
        for (int i = 0; i < numberOfServers ; i++) {

            Server server = new Server(i);
            servers.add(server);
            Client client = new Client(i);
            clients.add(client);
            Link link = new Link(client,server,propDelay,bw,1);
            links.add(link);
            client.setLink(link);
            server.getLinks().put(client,link);
        }
    }

    private static void setServersHavingFile(int numberOfServers, Map<Integer, List<Server>> serversHavingFile) {
        for (int i = 0; i < numberOfServers ; i++) {
            servers.get(i).setServersHavingFile(serversHavingFile);
        }
    }

    private static void assignFileListsToServers(int numberOfFiles, int numberOfServers, int numberOfFilesPerServer) {
        int fileId;
        for (int i = 0; i < numberOfServers ; i++) {
            List<IFile> fileList = new ArrayList<>(1);
            for (int j = 0; j < numberOfFilesPerServer; j++) {
                    fileId = i+j;
                    if (fileId>=numberOfFiles){
                        fileId-=numberOfFiles;
                    }
                    if (files.get(fileId)==null) throw new RuntimeException();
                    fileList.add(files.get(fileId));
            }
            servers.get(i).setFiles(fileList);
        }
    }

    private static void createFiles(int numberOfFiles, int sizeOfFiles) {
        for (int i = 0; i < numberOfFiles; i++) {
            files.add(new IFile(i,sizeOfFiles));
        }
    }

    private static void resetSimulatorSettings() {
        servers = new ArrayList<>();
        clients= new ArrayList<>();
        files= new ArrayList<>();
        links= new ArrayList<>();
        NetworkGraph.renewNetworrkGraph();
        networkGraph = NetworkGraph.networkGraph;
        Client.generatedId=0;
        RedirectingAlgorithm.rnd = new Random() ;
        DefaultValues.random = new Random() ;
    }
}
