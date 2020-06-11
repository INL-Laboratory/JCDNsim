/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

import entities.Network.NetworkGraph;
import entities.Setting.Configuration;
import entities.Setting.DefaultValues;
import entities.Setting.AlgParamsList;
import entities.Setting.RunningParameters;
import entities.Statistics.Result;
import entities.Statistics.Chart;
import entities.Utilities.Poisson;
import entities.Utilities.ZipfGenerator;
import entities.Utilities.logger.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


/**
 * This class is the main class of the simulator when you want to run it. In this class you can
 * specify what kind of simulation you want to perform.
 * RunSimulator initiates the files related to the simulation, creates and runs the instances of UnitSimulation and
 * controls the parallel or sequential running of the code.
 */
public class RunSimulator {
    static Calendar calendar = Calendar.getInstance();
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM uuuu HH:mm:ss");
    ArrayDeque<Callable<Float[]>> tasksBuffer = new ArrayDeque<>();
    ArrayList<Future<Float[]>> futures = new ArrayList<>();
    ArrayList<Result> results = new ArrayList<>();
    ArrayList<USimId> uSimIds = new ArrayList<>();


    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        String path = "results";
        path = initiateDirectory(path);
        createParametersFile(configuration, path);

        String photoPath = path+"/chart/photo.png";
        double startTime = System.currentTimeMillis();


        String runType;
        RunSimulator runSimulator= new RunSimulator();

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("available processors: "+ availableProcessors);
        System.out.println("Thread pool:* "+ 40);
        ExecutorService pool = Executors.newFixedThreadPool((40));

//        boolean[][] adjMat = NetworkGraph.generateLatticeTopology(configuration.numberOfServers);
        boolean[][] adjMat = NetworkGraph.usePowerLaw(configuration);

//        Formatter out = null;
//        try {
//            out = new Formatter(new File("hello.txt"));
//            for (int i = 0; i < adjMat.length; i++) {
//                for (int j = 0; j < adjMat.length; j++) {
//                    if (j!=adjMat.length - 1)
//                        out.format(adjMat[i][j]?"1 ": "0 ");
//                    else
//                        out.format(adjMat[i][j]?"1\n": "0\n");
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            out.close();
//        }




        Topology topology = NetworkGraph.getTopology(adjMat);
        // Run Section : If you want to run any simulations you want, you should modify this part.

        Number[] points1 = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
        Number[] points2 = {25,30,60,70,100};
//        runType = "Regular";
//        Map<String,Number> bundle1 = new HashMap<>();
//        bundle1.put(AlgParamsList.periodicStep.toString(),260);
//        bundle1.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
//        RunningParameters runParams= new RunningParameters(runType,"WMC", "periodic",bundle1,AlgParamsList.WMC_ALPHA.toString(), points1, configuration,path);
//        runSimulator.run(runParams,pool, topology);



//        runType = "Regular";
//        Map<String,Number> bundle2 = new HashMap<>();
//        bundle2.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
//        bundle2.put(AlgParamsList.periodicStep.toString(),260);
//        int[] caches = {10};
////
//        for (int i = 0; i < caches.length ; i++) {
//            configuration = new Configuration();
//            configuration.numberOfFilesPerServer=caches[i];
//            RunningParameters runParams2= new RunningParameters(runType,"HONEYBEE","piggyGroupedPeriodic",bundle2,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
//            runSimulator.run(runParams2,pool,topology);
//        }

//        runType = "Regular";
//        Map<String,Number> bundle4 = new HashMap<>();
//        bundle4.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
//        RunningParameters runParams4= new RunningParameters(runType,"WMC","ideal",bundle4,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
//        runSimulator.run(runParams4,pool,topology);



// Trade OFF
        runType = "Regular";
        Map<String,Number> bundle4 = new HashMap<>();
        bundle4.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
        bundle4.put(AlgParamsList.periodicStep.toString(),200);
        RunningParameters runParams4= new RunningParameters(runType,"HONEYBEE","piggyGroupedPeriodic",bundle4,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
        runSimulator.run(runParams4,pool,topology);

        runType = "Regular";
        Map<String,Number> bundle3 = new HashMap<>();
        RunningParameters runParams3= new RunningParameters(runType,"WMC","ideal"
                ,bundle3,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
        runSimulator.run(runParams3 ,pool,topology);


        runType = "Regular";
        Map<String,Number> bundle5 = new HashMap<>();
        bundle5.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
        bundle5.put(AlgParamsList.periodicStep.toString(),400);
        RunningParameters runParams5= new RunningParameters(runType,"WMC","periodic",bundle5,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
        runSimulator.run(runParams5,pool,topology);

        runType = "Regular";
        Map<String,Number> bundle6 = new HashMap<>();
        bundle6.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
        bundle6.put(AlgParamsList.periodicStep.toString(),0);
        RunningParameters runParams6= new RunningParameters(runType,"WMC","piggyBack",bundle6,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
        runSimulator.run(runParams6,pool,topology);


//        TradeOff

//
//        runType = "Regular";
//        Map<String,Number> bundle7 = new HashMap<>();
//        bundle7.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
//        bundle7.put(AlgParamsList.periodicStep.toString(),400);
//        RunningParameters runParams7= new RunningParameters(runType,"WMC","periodic",bundle7,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
//        runSimulator.run(runParams7,pool,topology);
//
//
//        runType = "Regular";
//        Map<String,Number> bundle8 = new HashMap<>();
//        bundle8.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
//        bundle8.put(AlgParamsList.periodicStep.toString(),600);
//        RunningParameters runParams8= new RunningParameters(runType,"WMC","periodic",bundle8,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
//        runSimulator.run(runParams8,pool,topology);
//
//        runType = "Regular";
//        Map<String,Number> bundle9 = new HashMap<>();
//        bundle9.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
//        bundle9.put(AlgParamsList.periodicStep.toString(),900);
//        RunningParameters runParams9= new RunningParameters(runType,"WMC","periodic",bundle9,AlgParamsList.WMC_ALPHA.toString(),points1,configuration,path);
//        runSimulator.run(runParams9,pool,topology);
//

//


        //P
//
//        Number[] points3 = {100,150,200,250,300,400,500,600,700};
//        runType = "P";
//        Map<String,Number> bundle1 = new HashMap<>();
//        bundle1.put(AlgParamsList.WMC_ALPHA.toString(),0.5);
//        bundle1.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
//        RunningParameters runParams= new RunningParameters(runType,"WMC", "periodic",bundle1,AlgParamsList.periodicStep.toString(), points3, configuration,path);
//        runSimulator.run(runParams,pool, topology);
//
//        Number[] points1 = {0,0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,0.1};
//        runType = "D";
//        Map<String,Number> bundle1 = new HashMap<>();
//        bundle1.put(AlgParamsList.WMC_ALPHA.toString(),0.5);
//        bundle1.put(AlgParamsList.periodicStep.toString(),500);
//        RunningParameters runParams= new RunningParameters(runType, "HONEYBEE","piggyGroupedPeriodic",bundle1,AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(), points1, configuration,path);
//        runSimulator.run(runParams,pool, topology);


        //End of Run Section

        finalizeRun(args, photoPath, startTime, runType, runSimulator);

    }

    private static void finalizeRun(String[] args, String photoPath, double startTime, String runType, RunSimulator runSimulator) throws IOException {
        Chart.initiateChart(photoPath,runType);


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

    private static void createParametersFile(Configuration configuration, String path) throws IOException {
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
    }

    private static String initiateDirectory(String path) {
        new File( path).mkdir();
        path = "results/"+simpleDateFormat.format(calendar.getTime());
        new File( path).mkdir();
        new File(path+"/logs").mkdir();
        new File(path+"/chart").mkdir();
        return path;
    }

    private void saveResults() throws IOException {
        for (Result result:results) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(result.timeStamp+"/"+result.simulationName+".dat"));
            objectOutputStream.writeObject(result);
            objectOutputStream.close();
        }
    }

    private void sequentialRun() throws Exception {
            Callable<Float[]> callable = tasksBuffer.removeFirst();
            UnitSimulation uSim = (UnitSimulation) callable;
            uSimIds.add(uSim.uSimID);
            Float[] res = callable.call();
            results.get(uSim.uSimID.id).putStatsInTables(uSim.uSimID.ithPoint,uSim.uSimID.jthRun,res);

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


    /** This method gets the simulation name from the responsible method, calls the  results' container generator.
     * The specific task of this method is to break the simulation into replications and to call the replication runner.
     * @param runParams
     * @param pool
     * @param topology
     * @throws Exception
     */
    private void run(RunningParameters runParams, ExecutorService pool,Topology topology) throws Exception {
        String simulationName;
        simulationName = getSimulationName(runParams);

        Result result = generateResultContainer(runParams, simulationName,topology.costNormalizationFactor);


        for (int j = 0; j < runParams.configuration.numberOfRuns ; j++) {
            runReplication(runParams, pool, topology, simulationName, result, j);
        }
    }

    private Result generateResultContainer(RunningParameters runParams, String simulationName, float costFactor) {
        Result result = new Result(runParams.valuesOfVariableParam.length, runParams.configuration.numberOfRuns,
                runParams.valuesOfVariableParam,runParams.RunType,simulationName,runParams.path);
        results.add(result);
        result.costNormalizationFactor = costFactor;
        return result;
    }

    /** This method breaks the replications into different points of it. It does so by creating unit simulations.
     * Then, it calls the method in charge of planning to run the unit simulations in an either parallel or sequential way.
     * Before running a replication it is needed to generate requests and distribute files in the CDN.
     * @param runParams
     * @param pool
     * @param topology
     * @param simulationName
     * @param result
     * @param jthReplication
     * @throws Exception
     */
    private void runReplication(RunningParameters runParams, ExecutorService pool, Topology topology,
                                String simulationName, Result result, int jthReplication) throws Exception {
        //Generating the requests and distributing the files before running the replication
        List<RequestEvent> requestEvents = generateRequests(runParams.configuration);
        int[][] serverContents = returnFilesDistribution(runParams);
        for (int i = 0; i < runParams.valuesOfVariableParam.length; i++){
            createLog(runParams.path, jthReplication, i);
            USimId uSimId = new USimId(i, jthReplication, results.size() - 1, simulationName, runParams.path);
            initiateUnitSimulation(runParams, i,uSimId, topology, result, requestEvents, serverContents);
            planTheRun(runParams, pool);
        }
    }


    private void planTheRun(RunningParameters runParams, ExecutorService pool) throws Exception {
        if (DefaultValues.RUN_PARALLEL) {
            int bufferSize = tasksBuffer.size();
            UnitSimulation recentlyAddedTask = (UnitSimulation) tasksBuffer.getLast();
            boolean isLastTaskAdded = isLastTaskAdded(runParams.valuesOfVariableParam, runParams.configuration, recentlyAddedTask);

            if (bufferSize == 500 || isLastTaskAdded) {
                parallelRun(pool, bufferSize);
            }
        }else {
            sequentialRun();
        }
    }

    private int[][] returnFilesDistribution(RunningParameters runParams) {
        return ZipfGenerator.returnFileList(1,runParams.configuration.numberOfFilesPerServer,runParams.configuration.numberOfFiles,runParams.configuration.numberOfServers);
    }

    private void createLog(String path, int j, int i) throws IOException {
        PrintWriter logger = null;
        if (DefaultValues.LOGGER_ON) {
            new File(path+"/logs/run"+j).mkdir();
            logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
            Logger.printWriter = logger;
        }
    }

    private void initiateUnitSimulation(RunningParameters runParam, int ithPoint, USimId uSimId1, Topology topology, Result result, List<RequestEvent> requestEvents, int[][] serverContents) {
        USimId uSimId = uSimId1;
        UnitSimulation unitSimulation = new UnitSimulation(uSimId, runParam,result, runParam.valuesOfVariableParam[ithPoint],requestEvents,serverContents, topology);
        tasksBuffer.addLast(unitSimulation);
    }


    private boolean isLastTaskAdded(Number[] valuesOfVariableParam, Configuration configuration, UnitSimulation lastAddedTask) {
        boolean isTheLastPointOfChart = lastAddedTask.uSimID.ithPoint == valuesOfVariableParam.length - 1;
        boolean isTheLastRun = lastAddedTask.uSimID.ithPoint == valuesOfVariableParam.length - 1;

        return isTheLastPointOfChart && isTheLastRun;
    }

    /**
     * This method is in charge of running the futures that are generated by the method extractTasks.
     * Running the futures is done by the passed ExecutorService.
     * @param pool The ExecutorService in which the futures run.
     * @param bufferSize The size of the buffer
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void parallelRun(ExecutorService pool, int bufferSize) throws InterruptedException, ExecutionException {
        int counter = extractTasks(pool);
        for (int i=bufferSize-1; i >=0; i--) {
            Float[] res = futures.get(counter-i).get();
            USimId uSimID = uSimIds.get(counter-i);
            results.get(uSimID.id).putStatsInTables(uSimID.ithPoint, uSimID.jthRun, res);
        }
    }

    /**
     * This method extracts the tasks put in the tasksBuffer and generates the corresponding futures.
     * The it submits the futures in the pool.
     * Running the futures is done by the passed ExecutorService.
     * @param pool The ExecutorService in which the futures run.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private int extractTasks(ExecutorService pool) {
        int counter = 0;
        while (!tasksBuffer.isEmpty()){
            Callable<Float[]> callable = tasksBuffer.removeFirst();
            UnitSimulation uSim = (UnitSimulation) callable;
            uSimIds.add(uSim.uSimID);
            Future<Float[]> future = pool.submit(callable);
            futures.add(future);
            counter = futures.size()-1;
        }
        return counter;
    }

    private List<RequestEvent> generateRequests(Configuration configuration) {
        Random random = new Random();
        List<RequestEvent> requests = new LinkedList<>();
        int reqFileId, requestingClientID;
        double timePerReq=  DefaultValues.SERVICE_TIME / configuration.numberOfServers / configuration.lambdaInOutRatio;
        double lambda = 1d/timePerReq;
        Poisson poisson = new Poisson(lambda,random);
        float reqTime = 0f;
        for (int  j= 0; j < configuration.numberOfRequests; j++) {

            requestingClientID = random.nextInt(configuration.numberOfServers);
            reqFileId = random.nextInt(configuration.numberOfFiles);

            reqTime = getInterarrivalTime(configuration ,random, reqTime , lambda, poisson);
            requests.add(
                    new RequestEvent(requestingClientID, reqFileId, reqTime)
            );
        }
        return requests;
    }

    private float getInterarrivalTime( Configuration configuration,Random random, float reqTime , double lambda , Poisson poisson) {
        if(!configuration.poissonArrivalsActivated) {
            reqTime += random.nextFloat() * 2 * (1d/lambda);
        }else
            reqTime+= poisson.getNextTime();
        return reqTime;
    }


    private String getSimulationName(RunningParameters runParams ) {
        String simulationName;
        StringBuffer simName = new StringBuffer();
        simName.append(runParams.RunType).append("-").append(runParams.algorithm).append("-").append(runParams.updateType).append("-");
        for (String fp:runParams.fixedParamsBundle.keySet()) {
            simName.append(fp).append("-").append(runParams.fixedParamsBundle.get(fp)).append("-");
        }
        simName.append(runParams.variableParam).append("-").append(runParams.valuesOfVariableParam[0]).append(" to ").append(runParams.valuesOfVariableParam[runParams.valuesOfVariableParam.length-1]);
        simName.append("-cacheSize-"+runParams.configuration.numberOfFilesPerServer);
        simName.append("-sites-"+runParams.configuration.numberofSites);
        simulationName = simName.toString();
        return simulationName;
    }




}
