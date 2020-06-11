/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

public class USimId {
    final int ithPoint , jthRun;
    final int id;
    final String simulationName;
    final String timeStamp;



    public USimId(int ithPoint, int jthRun, int id, String simulationName, String timeStamp) {
        this.ithPoint = ithPoint;
        this.jthRun = jthRun;
        this.id = id;
        this.simulationName = simulationName;
        this.timeStamp = timeStamp;
    }
}
