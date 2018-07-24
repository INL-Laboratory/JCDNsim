package entities.physical;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import edu.uci.ics.jung.graph.util.EdgeType;
import entities.logical.*;
import entities.utilities.Chart;
import entities.utilities.ZipfGenerator;
import entities.utilities.logger.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by hd on 2018/4/21 AD.
 */
public class ProjectRun {
    static List<Server> servers = new ArrayList<>();
    static List<Site> sites = new ArrayList<>();
    static List<Client>  clients= new ArrayList<>();
    static List<IFile>  files= new ArrayList<>();
    static List<Link>  links= new ArrayList<>();
    static NetworkGraph networkGraph = NetworkGraph.networkGraph;



    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss");
    private static LocalDateTime now = LocalDateTime.now();


    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        Configuration configuration = new Configuration();
        String path = "results";
        new File( path).mkdir();
        path = "results/"+dtf.format(now);
        new File( path).mkdir();
        new File(path+"/logs").mkdir();
        new File(path+"/chart").mkdir();
        PrintWriter parametersFile = new PrintWriter(new FileWriter(path+"/parameters.txt"));
        parametersFile.println("# of Servers: " + configuration.numberOfServers);
        parametersFile.println("# of Run for each point: " + configuration.numberOfRuns);
        parametersFile.println("# of Requests: " + configuration.numberOfRequests);
        parametersFile.println("# of Files: " + configuration.numberOfFiles);
        parametersFile.println("# of Files per Server: " + configuration.numberOfFilesPerServer);
        parametersFile.println("Size of Files(KB): " + configuration.sizeOfFiles);
        parametersFile.println("Request Size(KB) : " + DefaultValues.REQUEST_SIZE);
        parametersFile.println("Update Package Size(KB) : " + DefaultValues.PIGGY_BACK_SIZE);
//        parametersFile.println("Update Period(In case of periodic update)(ms) : " + DefaultValues.periodicStep);
        parametersFile.println("Service Time(ms) " + DefaultValues.SERVICE_TIME);
        parametersFile.println("Request generation average interval(ms): Every " + DefaultValues.SERVICE_TIME/configuration.numberOfServers/configuration.lambdaInOutRatio);
        parametersFile.println("lambdaInPerOutRatio: " + configuration.lambdaInOutRatio);
        parametersFile.println("Propagation Delay(ms):" + (DefaultValues.LINK_DELAY_ALLOWED?configuration.propagationDelay:" inoperative."));
        parametersFile.println("BandWidth(KB/ms):" + (DefaultValues.LINK_DELAY_ALLOWED?configuration.bandwidth: " inoperative."));
        parametersFile.println("Time out(ms):  " + (DefaultValues.IS_TIME_OUT_ACTIVATED?DefaultValues.TIME_OUT:"Disabled"));
        parametersFile.close();

        String photoPathName = path+"/chart/photo.png";
        Chart.initiateChart(photoPathName);


        Number[] points = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
        Number[] points1 = new Number[configuration.numberOfServers];
        for (int i = 1; i <=configuration.numberOfServers ; i++) {
            points1[i-1] = i;
        }
//        DefaultValues.PIGGY_BACK_SIZE = 1f;
//        DefaultValues.PIGGY_BACK_SIZE = 10f;
//        run("WMC",null,"periodic",3000,points,configuration,path);
//        DefaultValues.PIGGY_BACK_SIZE = 100f;
//        run("WMC",null,"periodic",200,points,configuration,path);
//        run("WMC",null,"periodic",50,points,configuration,path);
//        run("WMC",null,"periodic",20,points,configuration,path);
//        run("WMC",null,"periodic",10,points,configuration,path);
//        run("WMC",null,"periodic",100,points,configuration,path);
//        run("WMC",null,"periodic",300,points,configuration,path);
//        run("HONEYBEE",0.01,"piggyGroupedPeriodic",300,points,configuration,path);
//        run("HONEYBEE",0.0001,"piggyGroupedPeriodic",300,points,configuration,path);
//        run("HONEYBEE",0.001,"piggyGroupedPeriodic",300,points,configuration,path);
        run("HONEYBEE",0.001,"piggyGroupedPeriodic",300,points,configuration,path);
//        run("WMC",null,"piggyBack",null,points,configuration,path);
//        run("PSS",null,"ideal",null,points,configuration,path);
//        run("WMC",null,"ideal",null,points,configuration,path);
//        run("WMC",null,"periodic",1000,points,configuration,path);
//        run("MCS",null,"ideal",null,points1,configuration,path);


//        SimulationParameters.updateType=UpdateType.periodic;
//        RedirectingAlgorithm.periodicStep = 15;
//        Float[] costStats0 = new Float[numberOfPoints];
//        Float[] delayStats0 = new Float[numberOfPoints];
//        Float[][] costStatsForAllRuns0 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] delayStatsForAllRuns0 = new Float[numberOfPoints][numberOfRuns];
//        simulateWMC(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result0, costStatsForAllRuns0, delayStatsForAllRuns0, costStats0, delayStats0);
//
////
//
//
//        SimulationParameters.updateType=UpdateType.piggyBack;
//        DefaultValues.HONEY_BEE_SEARCH_PROBABILITY=0.2;
//        numberOfPoints = 11;
//        Float[] costStats1 = new Float[numberOfPoints];
//        Float[] delayStats1 = new Float[numberOfPoints];
//        Float[][] delayStatsForAllRuns1 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] costStatsForAllRuns1 = new Float[numberOfPoints][numberOfRuns];
//        simulateHoneyBee(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result1, costStatsForAllRuns1, delayStatsForAllRuns1, costStats1, delayStats1);
//
//        SimulationParameters.updateType=UpdateType.periodic;
//        RedirectingAlgorithm.periodicStep = 1000;
////        DefaultValues.HONEY_BEE_SEARCH_PROBABILITY=0.2;
//        numberOfPoints = 11;
//        Float[] costStats2 = new Float[numberOfPoints];
//        Float[] delayStats2 = new Float[numberOfPoints];
//        Float[][] delayStatsForAllRuns2 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] costStatsForAllRuns2 = new Float[numberOfPoints][numberOfRuns];
//        simulateWMC(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result1, costStatsForAllRuns2, delayStatsForAllRuns2, costStats2, delayStats2);
//
//        SimulationParameters.updateType=UpdateType.periodic;
//        RedirectingAlgorithm.periodicStep = 200;
//        numberOfPoints = 11;
//        Float[] costStats3 = new Float[numberOfPoints];
//        Float[] delayStats3 = new Float[numberOfPoints];
//        Float[][] delayStatsForAllRuns3 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] costStatsForAllRuns3 = new Float[numberOfPoints][numberOfRuns];
//        simulateWMC(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result1, costStatsForAllRuns3, delayStatsForAllRuns3, costStats3, delayStats3);
////
//
//        SimulationParameters.updateType=UpdateType.periodic;
//        DefaultValues.HONEY_BEE_SEARCH_PROBABILITY=50;
//        numberOfPoints = 11;
//        Float[] costStats4 = new Float[numberOfPoints];
//        Float[] delayStats4 = new Float[numberOfPoints];
//        Float[][] delayStatsForAllRuns4 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] costStatsForAllRuns4 = new Float[numberOfPoints][numberOfRuns];
//        simulateWMC(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result1, costStatsForAllRuns4, delayStatsForAllRuns4, costStats4, delayStats4);
//
//
//
//        SimulationParameters.updateType=UpdateType.piggyBack;
//        DefaultValues.HONEY_BEE_SEARCH_PROBABILITY=30;
//        numberOfPoints = 11;
//        Float[] costStats5 = new Float[numberOfPoints];
//        Float[] delayStats5 = new Float[numberOfPoints];
//        Float[][] costStatsForAllRuns5 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] delayStatsForAllRuns5 = new Float[numberOfPoints][numberOfRuns];
//        simulateWMC(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result2, costStatsForAllRuns5, delayStatsForAllRuns5, costStats5, delayStats5);




        //Chart stuff
//        String pathName = path+"/chart/photo.png" ,
//                seriesName0 = "WMC-Ideal",
//                seriesName1 = "Honeybee- 0.1",
//                seriesName2 = "WMC-piggyback",
//                seriesName3 = "WMC-periodic",
//                seriesName4 = "Honeybee- 0.7",
//                seriesName5 = "Honeybee- 0.95";
//        Chart.initiateChart(pathName);
//        Chart.addSeries(seriesName0, costStats0, delayStats0);
//        Chart.addSeries(seriesName1, costStats1, delayStats1);
//        Chart.addSeries(seriesName2, costStats2, delayStats2);
//        Chart.addSeries(seriesName3, costStats3, delayStats3);
//        Chart.addSeries(seriesName4, costStats4, delayStats4);
//        Chart.addSeries(seriesName5, costStats5, delayStats5);
        Chart.main(args);

    }
//    private static void simulatePSS(int numberOfPoints,int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, int numberOfRequests, float bandwidth, float propagationDelay, int sizeOfFiles, int numberOfRuns, float lambdaInOutRatio, String path, PrintWriter result, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, Float[] costStats, Float[] delayStats) throws IOException {
//        double startTime = System.currentTimeMillis();
//        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.PSS;
//        result.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
//        DefaultValues.PSS_PROBABILITY = 0;
//        for (int i = 0; i < numberOfPoints; i++){
//            System.out.println(i);
//
//            for (int j = 0; j < numberOfRuns ; j++) {
//                PrintWriter logger = null;
//                if (DefaultValues.LOGGER_ON) {
//                    new File(path+"/logs/run"+j).mkdir();
//                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
//                    Logger.printWriter = logger;
//                }
//                DefaultValues.PSS_PROBABILITY = 0.1d*i;
//                simulate(numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, lambdaInOutRatio, costStatsForAllRuns, delayStatsForAllRuns, i, j, logger);
//
//            }
//            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
//            result.print(DefaultValues.PSS_PROBABILITY);
//            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
//        }
//
//        double finishTime = System.currentTimeMillis();
//        result.println("Duration(min): " + (finishTime - startTime)/60000);
//
//        result.close();
//    }
//

//    private static void simulateWMC(int numberOfPoints,int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, int numberOfRequests, float bandwidth, float propagationDelay, int sizeOfFiles, int numberOfRuns, float lambdaInOutRatio, String path, PrintWriter result, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, Float[] costStats, Float[] delayStats) throws IOException {
//        double startTime = System.currentTimeMillis();
//        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.WMC;
//        result.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
//        DefaultValues.WMC_ALPHA = 0;
////        double a[] = {0.2d,0.1d,0.01d,0d};
////        float a = 0.2f;
//        for (int i = 0; i < numberOfPoints; i++){
//
//            for (int j = 0; j < numberOfRuns ; j++) {
//
//                PrintWriter logger = null;
//                if (DefaultValues.LOGGER_ON) {
//                    new File(path+"/logs/run"+j).mkdir();
//                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
//                    Logger.printWriter = logger;
//                }
//                System.out.println(i+" "+j);
////                DefaultValues.WMC_ALPHA = a[i];
////                DefaultValues.WMC_ALPHA = a - 0.02f*i;
//                DefaultValues.WMC_ALPHA = i*0.1f;
//                simulate(numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, lambdaInOutRatio, costStatsForAllRuns, delayStatsForAllRuns, i, j, logger);
//
//            }
//            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
//            result.print(DefaultValues.WMC_ALPHA);
//            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
//        }
//
//        double finishTime = System.currentTimeMillis();
//        result.println("Duration(min): " + (finishTime - startTime)/60000);
//
//        result.close();
//    }
//
//    private static void simulateHoneyBee(int numberOfPoints,int numberOfFiles, int numberOfServers, int numberOfFilesPerServer, int numberOfRequests, float bandwidth, float propagationDelay, int sizeOfFiles, int numberOfRuns, float lambdaInOutRatio, String path, PrintWriter result, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, Float[] costStats, Float[] delayStats) throws IOException {
//        double startTime = System.currentTimeMillis();
//        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.HONEYBEE;
//        result.println("Redirecting Algorithm : " + SimulationParameters.redirectingAlgorithmType);
//        DefaultValues.WMC_ALPHA = 0;
////        double a[] = {0.2d,0.1d,0.01d,0d};
////        float a = 0.2f;
//        for (int i = 0; i < numberOfPoints; i++){
//
//            for (int j = 0; j < numberOfRuns ; j++) {
//
//                PrintWriter logger = null;
//                if (DefaultValues.LOGGER_ON) {
//                    new File(path+"/logs/run"+j).mkdir();
//                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
//                    Logger.printWriter = logger;
//                }
//                System.out.println(i+" "+j);
////                DefaultValues.WMC_ALPHA = a[i];
////                DefaultValues.WMC_ALPHA = a - 0.02f*i;
//                DefaultValues.WMC_ALPHA = i*0.1f;
//                simulate(numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, lambdaInOutRatio, costStatsForAllRuns, delayStatsForAllRuns, i, j, logger);
//
//            }
//            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
//            result.print(DefaultValues.WMC_ALPHA);
//            result.print("\t cost: "+costStats[i]+ "\t delay: " + delayStats[i]+"\n");
//        }
//
//        double finishTime = System.currentTimeMillis();
//        result.println("Duration(min): " + (finishTime - startTime)/60000);
//
//        result.close();
//    }



    private static void run(@NotNull String algorithm, @Nullable Number honeyBeeProb , @NotNull String updateType , @Nullable Number periodicStep , Number[] points, Configuration configuration , String path) throws IOException, NoSuchFieldException, IllegalAccessException {
        Float[] costStats = new Float[points.length];
        Float[] delayStats = new Float[points.length];
        Float[][] costStatsForAllRuns = new Float[points.length][configuration.numberOfRuns];
        Float[][] delayStatsForAllRuns = new Float[points.length][configuration.numberOfRuns];
        String simulationName = algorithm+(honeyBeeProb==null?"":"-"+honeyBeeProb)+"-"+updateType+(periodicStep==null?"":"-"+periodicStep);

        double startTime = System.currentTimeMillis();
        PrintWriter result = new PrintWriter(new FileWriter(path+"/result"+simulationName+".txt"));
        String paramName = null ;
        String secParamName = null ;
        String terParamName = null ;
        switch (algorithm){
            case "WMC":
                paramName = "WMC_ALPHA";
                break;
            case "PSS":
                paramName = "PSS_PROBABILITY";
                break;
            case "MCS":
                paramName = "MCS_DELTA";
                break;
            case "HONEYBEE":
                paramName = "WMC_ALPHA";
                secParamName = "HONEY_BEE_SEARCH_PROBABILITY";
                break;
        }

        switch (updateType){
            case "piggyBack":
                break;
            case "periodic":
            case "piggyGroupedPeriodic":
                terParamName = "periodicStep";
                break;
            case "ideal":
                break;
        }

        Field algParam = DefaultValues.class.getField(paramName);
        try {
            Field secParam = DefaultValues.class.getField(secParamName);
            secParam.set(null, honeyBeeProb);
        }catch (Exception e){


        }

        try {
            Field terParam = DefaultValues.class.getField(terParamName);
            terParam.set(null, periodicStep);
        }catch (Exception e){


        }

        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.valueOf(algorithm);
        SimulationParameters.updateType = UpdateType.valueOf(updateType);
        result.println("Redirecting Algorithm : " + algorithm+"\n");
        if (secParamName!=null){result.println(secParamName+" : " + honeyBeeProb);}
        if (terParamName!=null){result.println(terParamName+" : " + periodicStep);}

        for (int i = 0; i < points.length; i++){
            for (int j = 0; j < configuration.numberOfRuns ; j++) {
                PrintWriter logger = null;
                if (DefaultValues.LOGGER_ON) {
                    new File(path+"/logs/run"+j).mkdir();
                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
                    Logger.printWriter = logger;
                }
                System.out.println(simulationName+"  "+i+"  "+"Run : "+j);
                algParam.set(null, points[i]);
                simulate(configuration, costStatsForAllRuns, delayStatsForAllRuns, i, j, logger);

            }
            calcAverageOnAllRuns(costStats,delayStats,costStatsForAllRuns, delayStatsForAllRuns,i);
            result.print(points[i]);
            result.printf("%10s %10f %10s %10f" ,"cost: ",costStats[i], "delay: ",delayStats[i]);
            result.println("");
        }

        Chart.addSeries(simulationName, costStats, delayStats);
        double finishTime = System.currentTimeMillis();
        result.println("Duration(min): " + (finishTime - startTime)/60000);

        result.close();
    }



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

    private static void simulate(Configuration configuration, Float[][] costStatsForAllRuns, Float[][] delayStatsForAllRuns, int i, int j, PrintWriter logger) {
//        double a = System.currentTimeMillis();
        initSimulator(configuration );
//        double b = System.currentTimeMillis();
//        System.out.println("initializing simulator :"+ (b-a));

        generateRequests(configuration);

//        double c = System.currentTimeMillis();
//        System.out.println("generating Requests : "+(c-b));
//        System.out.println("handling Events");
        Brain.handleEvents();
//        double d = System.currentTimeMillis();

//        System.out.println("Handle Events : "+(d-c));
//        System.out.println(EventsQueue.maximumTime);
        EventsQueue.maximumTime=0;
//        System.out.println(NetworkGraph.networkGraph.c);
//        System.out.println(NetworkGraph.networkGraph.t);
//        System.out.println("gathering Stats");
        gatherStats(costStatsForAllRuns, delayStatsForAllRuns, i,j);
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
    private static void generateRequests(Configuration configuration) {
//        double tempTime = System.currentTimeMillis();
        int reqFileId, requestingClientID;
        Random random = new Random();
        double timePerReq=  DefaultValues.SERVICE_TIME / configuration.numberOfServers / configuration.lambdaInOutRatio;
        double lambda = 1d/timePerReq;
        Poisson poisson = new Poisson(lambda,random);
        float reqTime = 0f;
        for (int  j= 0; j < configuration.numberOfRequests; j++) {

            requestingClientID = DefaultValues.random.nextInt(configuration.numberOfServers);
            reqFileId = DefaultValues.random.nextInt(configuration.numberOfFiles);

            reqTime = getInterarrivalTime( random, reqTime , lambda, poisson);
            EventsQueue.addEvent(
                    new Event(EventType.sendReq, clients.get(requestingClientID), reqTime, null, reqFileId)
            );
        }
//        totalTimeInGenerateRequests += System.currentTimeMillis()-tempTime;

    }

    private static float getInterarrivalTime( Random random, float reqTime , double lambda , Poisson poisson) {
        if(!DefaultValues.poissonArrivalsActivated) {
            reqTime += random.nextFloat() * 2 * (1d/lambda);
        }else
            reqTime+= poisson.getNextTime();
        return reqTime;
    }

    static double initiationTime;

    private static void initSimulator(Configuration configuration) {
         double EnteringTime = System.currentTimeMillis();

        resetSimulatorSettings();

        createFiles(configuration.numberOfFiles, configuration.sizeOfFiles);


        initiateServesAndClients(configuration.numberOfServers, configuration.propagationDelay, configuration.bandwidth);
        initiateSites(configuration.numberOfServers, configuration.numberofSites);

        Map<Integer, List<Server>> serversHavingFile = new HashMap<>();

        setServersHavingFile(configuration.numberOfServers, serversHavingFile);

        assignFileListsToServers(configuration.numberOfFiles,configuration. numberOfServers,configuration. numberOfFilesPerServer);

        createSimpleTopology(configuration.numberOfServers,configuration. propagationDelay,configuration. bandwidth);

        addLinksToGraph();

        fillServersHavingFile(serversHavingFile);
        setServerLoadLists(configuration.numberOfServers);
        networkGraph.buildRoutingTables();
         initiationTime = System.currentTimeMillis() - EnteringTime;

    }

    private static void initiateSites(int numberOfServers, int numberofSites) {
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
    private static void setServerLoadLists(int numberOfServers) {
        for (int i = 0; i < numberOfServers ; i++) {
            for (int j = 0; j < numberOfServers; j++) {
                servers.get(i).getServerLoadListss().put(servers.get(j),0);
            }
        }
    }

    private static void assignFileListsToServers(int numberOfFiles, int numberOfServers, int numberOfFilesPerServer) {
        int[][] serverContents = ZipfGenerator.returnFileList(1,numberOfFilesPerServer,numberOfFiles,numberOfServers);
        for (int i = 0; i < numberOfServers ; i++) {
            List<IFile> fileList = new LinkedList<>();
            for (int j = 0; j < serverContents[i].length ; j++) {
                fileList.add(files.get(serverContents[i][j]));
            }
            servers.get(i).setFiles(fileList);
        }
    }

    private static void createFiles(int numberOfFiles, float sizeOfFiles) {
        for (int i = 0; i < numberOfFiles; i++) {
            files.add(new IFile(i,sizeOfFiles));
        }
    }

    public static void sendPeriodicUpdate(float time, boolean groupedPeriodic) {
//        System.out.println(time + " periodic updates are being sent");
        for (int i = 0; i < servers.size(); i++) {
                if (!groupedPeriodic) servers.get(i).sendUpdateToAll(time,servers);
                else {
                    servers.get(i).sendUpdateToAll(time, servers.get(i).getSite().getServers());
                }
        }
    }

    private static void resetSimulatorSettings() {
        servers = new ArrayList<>();
        clients= new ArrayList<>();
        files= new ArrayList<>();
        links= new ArrayList<>();
        EventsQueue.lastSentPeriod =0;
        NetworkGraph.renewNetworrkGraph();
        networkGraph = NetworkGraph.networkGraph;
        Client.generatedId=0;
        RedirectingAlgorithm.rnd = new Random() ;
        DefaultValues.random = new Random() ;
    }
}
