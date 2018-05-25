package entities.logical;

import java.util.Random;

/**
 * Created by hd on 2018/3/31 AD.
 */
public class DefaultValues {
    public static final float PIGGY_BACK_SIZE = 1000f;
//    public static  int CACHE_SIZE = 100000;        // 100 GB , 100000 MB
    public static  float REQUEST_SIZE = (float) 0.1;
    public static  float SERVICE_TIME = 10f;
    public static  double PSS_PROBABILITY = 0.5f;
    public static double WMC_ALPHA = 0d;
    public static  int MCS_DELTA = 3;
    public static  Random random = new Random();


    public static final float TIME_OUT = 20f ;
    public static final boolean IS_TIME_OUT_ACTIVATED = false ;



    public static final boolean LINK_DELAY_ALLOWED= false;
    public static final boolean LOGGER_ON = false ;
    //Having it On takes over 850 MB of your disk for every 10 times making 20000 requests
}
