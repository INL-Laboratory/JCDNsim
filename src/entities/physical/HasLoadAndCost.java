package entities.physical;

import java.util.Map;

public interface HasLoadAndCost {
    Map<EndDevice, Integer> getCommunicationCostTable();

}
