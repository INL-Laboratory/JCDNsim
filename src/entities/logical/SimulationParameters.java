package entities.logical;

import entities.physical.NetworkGraph;
import entities.physical.Server;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hd on 2018/4/2 AD.
 */
public class SimulationParameters {
    public static final RedirectingAlgorithmType redirectingAlgorithmType = RedirectingAlgorithmType.PSS;
    public static final Map<Integer, List<Server>> serversHavingFile = new HashMap<>();
    public static File servers;
    public static File clients;
    public static File links;
    public static File topology;
    public static File serverContents;
    public static File requests;


}
