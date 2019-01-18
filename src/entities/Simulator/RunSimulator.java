package entities.Simulator;

import com.sun.istack.internal.NotNull;
import entities.Setting.Configuration;
import entities.Setting.DefaultValues;
import entities.Setting.ParamsChangibleRuntime;
import entities.Statistics.Result;
import entities.Statistics.Chart;
import entities.Utilities.Poisson;
import entities.Utilities.ZipfGenerator;
import entities.Utilities.logger.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


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


        String RunType;
        RunSimulator runSimulator= new RunSimulator();

        int availableProcerssors = (int)Runtime.getRuntime().availableProcessors();
        System.out.println("available processors: "+ availableProcerssors);
        ExecutorService pool = Executors.newFixedThreadPool((int)(availableProcerssors));

        
        
        boolean[][] latticeTopology = generateLatticeTopology(configuration.numberOfServers);

        // Run Section : If you want to run any simulations you want, you should modify this part.


        Number[] points1 = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
        RunType = "Regular";
        Map<String,Number> bundle1 = new HashMap<>();
        bundle1.put(ParamsChangibleRuntime.periodicStep.toString(),400);
        bundle1.put(ParamsChangibleRuntime.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
        runSimulator.run(RunType,"WMC","periodic"
                ,bundle1,ParamsChangibleRuntime.WMC_ALPHA.toString(),points1,configuration,path,pool, latticeTopology);



        RunType = "Regular";
        Map<String,Number> bundle7 = new HashMap<>();
        bundle7.put(ParamsChangibleRuntime.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
        bundle7.put(ParamsChangibleRuntime.periodicStep.toString(),20);
        runSimulator.run(RunType,"HONEYBEE","piggyGroupedPeriodic"
                ,bundle7,ParamsChangibleRuntime.WMC_ALPHA.toString(),points1,configuration,path,pool,latticeTopology);

        RunType = "Regular";
        Map<String,Number> bundle8 = new HashMap<>();
        runSimulator.run(RunType,"WMC","ideal"
                ,bundle8,ParamsChangibleRuntime.WMC_ALPHA.toString(),points1,configuration,path,pool,latticeTopology);

        //End of Run Section

        Chart.initiateChart(photoPath,RunType);


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

    private void submitFuture() throws ExecutionException, InterruptedException {
        int availableProccerssors = (int)Runtime.getRuntime().availableProcessors();
        System.out.println("available proccessors: "+ availableProccerssors);
        ExecutorService pool = Executors.newFixedThreadPool((int)(availableProccerssors));
        while (!tasksBuffer.isEmpty()){
            Callable<Float[]> callable = tasksBuffer.removeFirst();
            UnitSimulation uSim = (UnitSimulation) callable;
            uSimIds.add(uSim.uSimID);
            Future<Float[]> future = pool.submit(callable);
            futures.add(future);
//            System.gc();
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



    private void run(String RunType,@NotNull String algorithm, @NotNull String updateType, Map<String,Number> fixedParamsBundle, String variableParam,Number[] valuesOfVariableParam,Configuration configuration , String path, ExecutorService pool,boolean[][] topology) throws Exception {
        String simulationName;
        simulationName = getSimulationName(RunType, algorithm, updateType, fixedParamsBundle, variableParam, valuesOfVariableParam,configuration);

        Result result = new Result(valuesOfVariableParam.length, configuration.numberOfRuns,valuesOfVariableParam,RunType,simulationName,path);
        results.add(result);


        for (int j = 0; j < configuration.numberOfRuns ; j++) {
            Random random = new Random();
            List<RequestEvent> requestEvents = generateRequests(configuration, random);

            int[][] serverContents = ZipfGenerator.returnFileList(1,configuration.numberOfFilesPerServer,configuration.numberOfFiles,configuration.numberOfServers);

            for (int i = 0; i < valuesOfVariableParam.length; i++){
                PrintWriter logger = null;
                if (DefaultValues.LOGGER_ON) {
                    new File(path+"/logs/run"+j).mkdir();
                    logger = new PrintWriter(new FileWriter(path + "/logs/run" + j + "/with i " + i + ".txt"));
                    Logger.printWriter = logger;
                }
                USimId uSimId = new USimId(i,j,results.size()-1,simulationName,path);
                UnitSimulation unitSimulation = new UnitSimulation(uSimId,configuration,result,algorithm,updateType,valuesOfVariableParam[i],variableParam,fixedParamsBundle,requestEvents,serverContents, topology);
                tasksBuffer.addLast(unitSimulation);


                if (DefaultValues.RUN_PARALLEL) {
                    int bufferSize = tasksBuffer.size();
                    UnitSimulation lastAddedTask = (UnitSimulation) tasksBuffer.getLast();
                    boolean isLastTaskAdded = lastAddedTask.uSimID.ithPoint == (valuesOfVariableParam.length - 1) && lastAddedTask.uSimID.jthRun == (configuration.numberOfRuns - 1);

                    if (bufferSize == 500 || isLastTaskAdded) {
                        parallelRun(pool, bufferSize);

                    }
                }else {
                    sequentialRun();
                }



            }
        }
    }

    private static boolean[][] generateLatticeTopology(int n) {
        boolean[][] top = new boolean[n][n];
        int m = (int)Math.sqrt(n);
        if (Math.sqrt(n)!=m) throw new RuntimeException("Square number of servers is needed");
        for (int i = 0; i < n ; i++) {
            int[] neighbours= {i%m==0?i+m-1:i-1
                    ,i%m==m-1?i-m+1:i+1
                    ,i<m?n-m+i:i-m
                    ,n-1-i<m?i-(n-m):i+m};
            for (int j = 0; j < neighbours.length ; j++) {
                if (neighbours[j]<i) continue;
                top[i][neighbours[j]] =  true;
            }
        }
        return top;
    }

    private void parallelRun(ExecutorService pool, int bufferSize) throws InterruptedException, ExecutionException {
        int c = 0;
        while (!tasksBuffer.isEmpty()){
            Callable<Float[]> callable = tasksBuffer.removeFirst();
            UnitSimulation uSim = (UnitSimulation) callable;
            uSimIds.add(uSim.uSimID);
            Future<Float[]> future = pool.submit(callable);
            futures.add(future);
            c = futures.size()-1;
        }
        for (int k=bufferSize-1; k >=0; k--) {
            Float[] res = futures.get(c-k).get();
            USimId uSimID = uSimIds.get(c-k);
            results.get(uSimID.id).putStatsInTables(uSimID.ithPoint, uSimID.jthRun, res);
        }
    }

    private List<RequestEvent> generateRequests(Configuration configuration, Random random) {
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


    private String getSimulationName(String RunType, @NotNull String algorithm, @NotNull String updateType, Map<String, Number> fixedParamsBundle, String variableParam, Number[] valuesOfVariableParam,Configuration configuration) {
        String simulationName;
        StringBuffer simName = new StringBuffer();
        simName.append(RunType).append("-").append(algorithm).append("-").append(updateType).append("-");
        for (String fp:fixedParamsBundle.keySet()) {
            simName.append(fp).append("-").append(fixedParamsBundle.get(fp)).append("-");
        }
        simName.append(variableParam).append("-").append(valuesOfVariableParam[0]).append(" to ").append(valuesOfVariableParam[valuesOfVariableParam.length-1]);
        simName.append("-cacheSize-"+configuration.numberOfFilesPerServer);
        simName.append("-sites-"+configuration.numberofSites);
        //TODO
        simulationName = simName.toString();
        return simulationName;
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
