package entities.Setting;

import java.util.Random;


/**
 * AlgorithmDate contains the setting of any algorithm run in the simulator. Parameters are shown and described below.
 */
public class AlgorithmData {
    public RedirectingAlgorithmType redirectingAlgorithmType;
    public UpdateType updateType;

    public Number PSS_PROBABILITY = null;
    public Number WMC_ALPHA = null;
    public Number HONEY_BEE_SEARCH_PROBABILITY = null;
    public Number MCS_DELTA = null;
    public Number periodicStep = null;
    public int Radius = 1 ;


//    public float PIGGY_BACK_SIZE = 1f;
    //    public static  int CACHE_SIZE = 100000;        // 100 GB , 100000 MB
//    public float REQUEST_SIZE = (float) 1;
//    public float SERVICE_TIME = 50f;
    public Random random = new Random();



//    public final float TIME_OUT = 20f ;
//    public final boolean IS_TIME_OUT_ACTIVATED = false ;





    public int generatedId= 0;


}
