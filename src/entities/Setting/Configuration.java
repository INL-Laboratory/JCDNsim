package entities.Setting;




public class Configuration {
    public final int numberOfFiles = 100;
    public final int numberOfServers = 100;
    public final int numberofSites = 25;
    public final int numberOfFilesPerServer = 10;       // each server contains how many files --cache size.
    public final int numberOfRequests =100000;     //number of requests
    public final float bandwidth = 2f;     //ms
    public final float propagationDelay = 0.1f; //ms
    public final float sizeOfFiles = 0.0001f;  //KB
    public final int numberOfRuns = 200;       //Number of replications of a specified algorithm
    public final float lambdaInOutRatio = 0.7f;    //The ratio by which we adjust the ratio of coming requests per served requests
    public final boolean poissonArrivalsActivated = true ;


}
//    final int numberOfFiles = 100;
//    final int numberOfServers = 100;
//    final int numberofSites = 25;
//    final int numberOfFilesPerServer = 7;
//    final int numberOfRequests =500000;
//    final float bandwidth = 2f;
//    final float propagationDelay = 0.1f;
//    final float sizeOfFiles = 0.0001f;
//    final int numberOfRuns = 150;
//    final float lambdaInOutRatio = 0.9f;


//
//public class Configuration {
//    final int numberOfFiles = 100;
//    final int numberOfServers = 100;
//    final int numberofSites = 10;
//    final int numberOfFilesPerServer = 7;       // each server contains how many files --cache size.
//    final int numberOfRequests =100000;     //number of requests
//    final float bandwidth = 2f;     //ms
//    final float propagationDelay = 0.1f; //ms
//    final float sizeOfFiles = 0.0001f;  //KB
//    final int numberOfRuns = 200;       //Number of replications of a specified algorithm
//    final float lambdaInOutRatio = 0.9f;    //The ratio by which we adjust the ratio of coming requests per served requests
//}
