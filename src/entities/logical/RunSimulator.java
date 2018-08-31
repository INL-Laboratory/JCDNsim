package entities.logical;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import entities.Statistics.Result;
import entities.Statistics.Chart;
import entities.utilities.logger.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.*;


public class RunSimulator {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss");
    private static LocalDateTime now = LocalDateTime.now();

    ArrayDeque<Callable<Float[]>> simulationsToBeRun = new ArrayDeque<>();
    ArrayList<Future<Float[]>> futures = new ArrayList<>();
    ArrayList<Result> results = new ArrayList<>();
    ArrayList<USimId> uSimIds = new ArrayList<>();


    public static void main(String[] args) throws Exception {
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
        parametersFile.println("# of sites: " + configuration.numberofSites);
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
        double startTime = System.currentTimeMillis();


        Number[] points = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
//        Number[] points = {0.5};
        Number[] points1 = new Number[configuration.numberOfServers];
        for (int i = 1; i <=configuration.numberOfServers ; i++) {
            points1[i-1] = i;
        }
//        Number[] points2 = new Number[20];
//        for (int i = 1; i <=20 ; i++) {
//            points2[i-1] = i;
//        }
        Number[] points2 = {0.1,0.2,0.3};
//        DefaultValues.PIGGY_BACK_SIZE = 1f;
//        DefaultValues.PIGGY_BACK_SIZE = 10f;
//        run("WMC",null,"periodic",3000,points,configuration,path);
//        DefaultValues.PIGGY_BACK_SIZE = 100f;
//        run("WMC",null,"periodic",200,points,configuration,path);
//        run("WMC",null,"periodic",50,points,configuration,path);
//        run("WMC",null,"periodic",20,points,configuration,path);
//        run("WMC",null,"periodic",10,points,configuration,path);
//        run("WMC",null,"periodic",100,points,configuration,path);
//        run("HONEYBEE",0.01,"piggyGroupedPeriodic",300,points,configuration,path);
//        run("HONEYBEE",0.0001,"piggyGroupedPeriodic",300,points,configuration,path);
//        run("HONEYBEE",0.001,"piggyGroupedPeriodic",300,points,configuration,path);
        RunSimulator runSimulator= new RunSimulator();

//        runSimulator.run("WMC",null,"periodic",1200,points,configuration,path);
//        runSimulator.run("WMC",null,"piggyBack",null,points,configuration,path);
//        runSimulator.run("WMC",null,"ideal",null,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",1000,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",1200,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",800,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",600,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",320,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",340,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",360,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",380,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",800,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",900,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",1000,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",1100,points,configuration,path);
//        runSimulator.run("WMC",null,"periodic",1200,points,configuration,path);
//        runSimulator.run("HONEYBEE",0,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.001,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.002,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.003,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.004,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.005,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.006,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.007,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.009,"piggyGroupedPeriodic",5000,points,configuration,path);
//        runSimulator.run("HONEYBEE",0.010,"piggyGroupedPeriodic",5000,points,configuration,path);
//        projectRun.run("PSS",null,"ideal",null,points,configuration,path);
//        run("WMC",null,"periodic",1000,points,configuration,path);
//        run("MCS",null,"ideal",null,points1,configuration,path);

        runSimulator.run("CostBased",null,"periodic",500,points,configuration,path);

//        SimulationParameters.updateType=UpdateType.periodic;
//        RedirectingAlgorithm.periodicStep = 15;
//        Float[] costStats0 = new Float[numberOfPoints];
//        Float[] delayStats0 = new Float[numberOfPoints];
//        Float[][] costStatsForAllRuns0 = new Float[numberOfPoints][numberOfRuns];
//        Float[][] delayStatsForAllRuns0 = new Float[numberOfPoints][numberOfRuns];
//        simulateWMC(numberOfPoints,numberOfFiles, numberOfServers, numberOfFilesPerServer, numberOfRequests, bandwidth, propagationDelay, sizeOfFiles, numberOfRuns, lambdaInOutRatio, path, result0, costStatsForAllRuns0, delayStatsForAllRuns0, costStats0, delayStats0);

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



        if (DefaultValues.RUN_PARALLEL) {
            runSimulator.submitFuture();
        }else {
            runSimulator.sequentialRun();

        }

        runSimulator.finalizeResults();
        runSimulator.saveResults();

        double finishTime = System.currentTimeMillis();
        System.out.println("Duration(min): " + (finishTime - startTime)/60000);



        try {
            Chart.main(args);
        }catch (Exception e){
            e.printStackTrace();
        }





    }

    private void saveResults() throws IOException {
        for (Result result:results) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(result.timeStamp+"/"+result.simulationName+".dat"));
            objectOutputStream.writeObject(result);
            objectOutputStream.close();
        }
    }

    private void sequentialRun() throws Exception {
        while (!simulationsToBeRun.isEmpty()){
            Callable<Float[]> callable = simulationsToBeRun.removeFirst();
            UnitSimulation uSim = (UnitSimulation) callable;
            uSimIds.add(uSim.uSimID);
            Float[] res = callable.call();
            results.get(uSim.uSimID.id).putStatsInTables(uSim.uSimID.ithPoint,uSim.uSimID.jthRun,res);
        }

    }


    private void submitFuture() throws ExecutionException, InterruptedException {
        int availableProccerssors = Runtime.getRuntime().availableProcessors();
        System.out.println("available proccessors: "+ availableProccerssors);
        ExecutorService pool = Executors.newFixedThreadPool((int)(availableProccerssors));
        while (!simulationsToBeRun.isEmpty()){
            Callable<Float[]> callable = simulationsToBeRun.removeFirst();
            UnitSimulation uSim = (UnitSimulation) callable;
            uSimIds.add(uSim.uSimID);
            Future<Float[]> future = pool.submit(callable);
            futures.add(future);
            System.gc();
        }
        for (int i = 0; i < futures.size() ; i++) {
            Float[] res = futures.get(i).get();
            USimId uSimID = uSimIds.get(i);
            results.get(uSimID.id).putStatsInTables(uSimID.ithPoint,uSimID.jthRun,res);

        }
        pool.shutdown();

    }

    private void finalizeResults() {
        for (int i = 0; i < results.size() ; i++) {
            results.get(i).calcAverageOnAllRunsOnAllPoints();
        }
        for (int i = 0; i < results.size() ; i++) {
            Result result = results.get(i);
            Chart.addSeries(result.simulationName,result.getCostStats(),result.getDelayStats());

        }
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


    private void run(@NotNull String algorithm, @Nullable Number honeyBeeProb , @NotNull String updateType , @Nullable Number periodicStep , Number[] points, Configuration configuration , String path) throws IOException, NoSuchFieldException, IllegalAccessException {
        String simulationName = algorithm+(honeyBeeProb==null?"":"-"+honeyBeeProb)+"-"+updateType+(periodicStep==null?"":"-"+periodicStep);
        Result result = new Result(points.length,configuration.numberOfRuns, simulationName,path);
        results.add(result);

//        double startTime = System.currentTimeMillis();
//        PrintWriter resultIO = new PrintWriter(new FileWriter(path+"/resultIO"+simulationName+".txt"));
        String paramName = null ;
        String secParamName = null ;
        String terParamName = null ;

        paramName = setAlgorithmNameParam(algorithm);
        secParamName = setHoneyBeeParam(algorithm);
        terParamName = setUpdateTypeParam(updateType);


//        resultIO.println("Redirecting Algorithm : " + algorithm+"\n");
//        if (secParamName!=null){resultIO.println(secParamName+" : " + honeyBeeProb);}
//        if (terParamName!=null){resultIO.println(terParamName+" : " + periodicStep);}

        for (int i = 0; i < points.length; i++){
            for (int j = 0; j < configuration.numberOfRuns ; j++) {
                PrintWriter logger = null;
                if (DefaultValues.LOGGER_ON) {
                    new File(path+"/logs/run"+j).mkdir();
                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
                    Logger.printWriter = logger;
                }
                USimId uSimId = new USimId(i,j,results.size()-1,simulationName,path);
                UnitSimulation unitSimulation = new UnitSimulation(uSimId,configuration,result,algorithm,updateType,points[i],honeyBeeProb, periodicStep, paramName,secParamName, terParamName);
                simulationsToBeRun.addLast(unitSimulation);

            }
//            result.calcAverageOnAllRuns(i);
            //TODO
//            resultIO.print(points[i]);
//            resultIO.printf("%10s %10f %10s %10f" ,"cost: ",costStats[i], "delay: ",delayStats[i]);
//            resultIO.println("");
        }

//        Chart.addSeries(simulationName, result.costStats, result.delayStats);
//        produceMatFile(simulationName,dtf.format(now), costStats, delayStats);
//        double finishTime = System.currentTimeMillis();
//        resultIO.println("Duration(min): " + (finishTime - startTime)/60000);

//        resultIO.close();
    }


    private String setAlgorithmNameParam(@NotNull String algorithm) {
        String paramName = null ;
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
            case "CostBased":
                paramName = "Radius";
                break;
            case "HONEYBEE":
                paramName = "WMC_ALPHA";
                break;
        }
        return paramName;
    }

    private String setHoneyBeeParam(@NotNull String algorithm) {
        String secParamName = null ;

        switch (algorithm){
            case "HONEYBEE":
                secParamName = "HONEY_BEE_SEARCH_PROBABILITY";
                break;
            default:
                break;
        }
        return secParamName;
    }

    private String setUpdateTypeParam(@NotNull String updateType) {
        String terParamName = null ;
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
        return terParamName;
    }


}
