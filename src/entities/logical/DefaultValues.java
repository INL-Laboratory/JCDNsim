package entities.logical;

import java.util.Random;

/**
 * Created by hd on 2018/3/31 AD.
 */
public class DefaultValues {
    public static final int SERVER_SERVER_LOCAL_WEIGHT = 1;
    public static final int SERVER_SERVER_INTERSITE_WEIGHT = 2;
    public static final int SERVER_CLIENT_WEIGHT = 1;
    public static  float PIGGY_BACK_SIZE = 1.5f;
//    public static  int CACHE_SIZE = 100000;        // 100 GB , 100000 MB
    public static  float REQUEST_SIZE = (float) 1.5;
    public static  float SERVICE_TIME = 50f;
//    public static  Random random = new Random();



    public static final float TIME_OUT = 20f ;
    public static final boolean IS_TIME_OUT_ACTIVATED = false ;






    public static final boolean LINK_DELAY_ALLOWED= true;
    public final static boolean LOGGER_ON = false ;
    public final static boolean RUN_PARALLEL = true;
    //Having it On takes over 850 MB of your disk for every 10 times making 20000 requests




}
