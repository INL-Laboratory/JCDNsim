package entities.physical;

import edu.uci.ics.jung.graph.util.EdgeType;
import entities.logical.*;
import entities.utilities.Chart;
import entities.utilities.logger.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    public static void main(String[] args) throws IOException {
        final int numberOfFiles = 25;
        final int numberOfServers = 25;
        final int numberOfFilesPerServer = 4;



        float[] costStats = new float[11];
        float[] delayStats = new float[11];
        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.PSS;
        DefaultValues.PSS_PROBABILITY = 0;

        for (int i = 0; i < 11; i++){
            PrintWriter printWriter = new PrintWriter(new FileWriter("run with alpha "+i+".txt"));
            Logger.printWriter = printWriter;
            DefaultValues.PSS_PROBABILITY = 0.1f*i;
            System.out.println(DefaultValues.PSS_PROBABILITY);
            initSimulator(numberOfFiles, numberOfServers, numberOfFilesPerServer);
            int reqFileId, requestingClientID;
            float reqTime = 0f;
//            int bound = (int) DefaultValues.SERVICE_TIME * 50;
            for (int  j= 0; j < 20000; j++) {

                requestingClientID = DefaultValues.random.nextInt(numberOfServers);
                reqFileId = DefaultValues.random.nextInt(numberOfFiles);

                reqTime += DefaultValues.random.nextFloat();
                EventsQueue.addEvent(
                        new Event(EventType.sendReq, clients.get(requestingClientID), reqTime, null, reqFileId)
                );
            }

            Brain.handleEvents();
//            Gathering Stats
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
            printWriter.close();
        }
        Chart.xValues = costStats;
        Chart.yValues = delayStats;
        Chart.main(args);
    }

    private static void initSimulator(int numberOfFiles, int numberOfServers, int numberOfFilesPerServer) {
        servers = new ArrayList<>();
        clients= new ArrayList<>();
        files= new ArrayList<>();
        links= new ArrayList<>();
        NetworkGraph.renewNetworrkGraph();
        networkGraph = NetworkGraph.networkGraph;
        Client.generatedId=0;

        for (int i = 0; i < numberOfFiles; i++) {
            files.add(new IFile(i,500));
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
            Link link = new Link(client,server,1f,100f,1);
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
                Link link = new Link(server,targetServer,1f,100f,1);
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
