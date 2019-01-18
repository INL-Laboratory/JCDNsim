package entities.Network;

import java.util.Map;

public interface HasLoadAndCost {
    Map<EndDevice, Integer> getCommunicationCostTable();

}
