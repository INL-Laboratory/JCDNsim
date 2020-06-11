/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Network;

import java.util.Map;

public interface HasLoadAndCost {
    Map<EndDevice, Integer> getCommunicationCostTable();

}
