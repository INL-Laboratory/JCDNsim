package entities.physical;

import java.util.Random;

/**
 * Created by hd on 2018/3/31 AD.
 */
public class DefaultValues {
    public static final int CACHE_SIZE = 100000;        // 100 GB , 100000 MB
    public static final float REQUEST_SIZE = (float) 0.05;
    public static final float SERVICE_TIME = 1f;
    public static final float PSS_PROBABILITY = 0.5f;
    public static final float WMC_ALPHA = 0.5f;
    public static final int MCS_DELTA = 3;
    public static final Random random = new Random();



}
