package entities.logical;

import java.util.Random;

public class AlgorithmData {
    public RedirectingAlgorithmType redirectingAlgorithmType;
    public UpdateType updateType;

    public Number PSS_PROBABILITY = 0.5f;
    public Number WMC_ALPHA = 0d;
    public Number HONEY_BEE_SEARCH_PROBABILITY = 0.2d;
    public Number MCS_DELTA = 3;
    public float periodicStep = 1000f;


//    public float PIGGY_BACK_SIZE = 1f;
    //    public static  int CACHE_SIZE = 100000;        // 100 GB , 100000 MB
//    public float REQUEST_SIZE = (float) 1;
//    public float SERVICE_TIME = 50f;
    public Random random = new Random();



//    public final float TIME_OUT = 20f ;
//    public final boolean IS_TIME_OUT_ACTIVATED = false ;


    public final boolean poissonArrivalsActivated = true ;



    public int generatedId= 0;


    public int Radius = 1 ;
}
