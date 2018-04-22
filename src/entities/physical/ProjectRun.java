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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final int numberOfFiles = 25;
        final int numberOfServers = 25;
        final int numberOfFilesPerServer = 4;
        final int numberOfRequests = 20000;
        final float bandwidth = 100f;
        final float propagationDelay = 1f;
        final int sizeOfFiles = 500;
        final float reqRateCoef = 1;
        String path = dtf.format(now);
        new File(path).mkdir();
        new File(path+"/logs").mkdir();
        new File(path+"/chart").mkdir();
        PrintWriter parametersFile = new PrintWriter(new FileWriter(path+"/parameters.txt"));
        PrintWriter result = new PrintWriter(new FileWriter(path+"/result.txt"));
        parametersFile.println("No of Servers: " + numberOfServers);
        parametersFile.println("No of Requests: " + numberOfRequests);
        parametersFile.println("No of Files: " + numberOfFiles);
        parametersFile.println("Size of Files: " + sizeOfFiles);
        parametersFile.println("No of Files per Server: " + numberOfFilesPerServer);
        parametersFile.println("Request Size(MB) : " + DefaultValues.REQUEST_SIZE);
        parametersFile.println("Service Time " + DefaultValues.SERVICE_TIME);
        parametersFile.println("Request Rate: float[0,1]* " + reqRateCoef);
        parametersFile.println("Propagation Delay:" + propagationDelay);
        parametersFile.println("BandWidth:" + bandwidth);
        parametersFile.println("Time out:  " + (DefaultValues.IS_TIME_OUT_ACTIVATED?"Enabled":"Disabled"));
        parametersFile.println((DefaultValues.IS_TIME_OUT_ACTIVATED?"Time out time: " + DefaultValues.TIME_OUT:""));
        parametersFile.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
        parametersFile.close();
        result.println("Redirecting Algorithm Parameter: " );

        String startDate = dtf.format(now);



        Float[] costStats = new Float[11];
        Float[] delayStats = new Float[11];



        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.PSS;
        DefaultValues.PSS_PROBABILITY = 0;
        for (int i = 0; i < 11; i++){
            PrintWriter logger = new PrintWriter(new FileWriter(path+"/logs/run with i "+i+".txt"));
            Logger.printWriter = logger;
            DefaultValues.PSS_PROBABILITY = 0.1f*i;
            result.print(DefaultValues.PSS_PROBABILITY);
            System.out.println(DefaultValues.PSS_PROBABILITY);
            initSimulator(numberOfFiles, numberOfServers, numberOfFilesPerServer , propagationDelay , bandwidth,sizeOfFiles );
            generateRequests(numberOfRequests,numberOfFiles, numberOfServers , reqRateCoef);
            Brain.handleEvents();
            gatherStats(costStats, delayStats, i);
            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
            logger.close();
        }

        String finishDate = dtf.format(now);
        result.println("Start on: " + startDate);
        result.println("Finish on: " + finishDate);

        result.close();


        //Chart stuff
        String pathName = path+"/chart/photo.png" , seriesName = SimulationParameters.redirectingAlgorithmType.toString();
        Chart.initiateChart(pathName);
        Chart.addSeries(seriesName, costStats, delayStats);
        Chart.main(args);

    }


    private static void gatherStats(Float[] costStats, Float[] delayStats, int i) {
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

        costStats[i] = totalCost/counter;
        delayStats[i]= totalDelay/counter;
    }

    private static void generateRequests(int numberOfRequests,int numberOfFiles, int numberOfServers, float coef) {
        int reqFileId, requestingClientID;
        float reqTime = 0f;
        for (int  j= 0; j < numberOfRequests; j++) {

            requestingClientID = DefaultValues.random.nextInt(numberOfServers);
            reqFileId = DefaultValues.random.nextInt(numberOfFiles);

            reqTime += DefaultValues.random.nextFloat()*coef;
            EventsQueue.addEvent(
                    new Event(EventType.sendReq, clients.get(requestingClientID), reqTime, null, reqFileId)
            );
        }
    }

    private static void initSimulator(int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, float propDelay, float bw, int sizeOfFiles) {
        servers = new ArrayList<>();
        clients= new ArrayList<>();
        files= new ArrayList<>();
        links= new ArrayList<>();
        NetworkGraph.renewNetworrkGraph();
        networkGraph = NetworkGraph.networkGraph;
        Client.generatedId=0;

        for (int i = 0; i < numberOfFiles; i++) {
            files.add(new IFile(i,sizeOfFiles));
        }

        int fileId =0;
        Map<Integer, List<Server>> serversHavingFile = new HashMap<>();
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

            Server server = new Server(i,fileList, serversHavingFile);
            servers.add(server);
            Client client = new Client(i);
            clients.add(client);
            Link link = new Link(client,server,propDelay,bw,1);
            links.add(link);
            client.setLink(link);
            server.getLinks().put(client,link);
        }

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

        StringBuffer sb = new StringBuffer();
        List<Server> serversss ;
        sb.append(" ***** Files ***** ");

        for(IFile f: files){
            serversss = networkGraph.getServersHavingFile(f.getId());
            serversHavingFile.put(f.getId(),serversss);
            sb.append("\n").append(f).append(" :");
            for(Server s: serversss){
                sb.append("  ").append(s);
            }
        }
        Logger.print(sb.toString(),0);
        networkGraph.buildRoutingTables();
    }
}
