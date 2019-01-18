/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Statistics;

import entities.Network.Client;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
     private Float[] costStats ;
     private Float[] delayStats ;
     private transient Float[][] costStatsForAllRuns ;
     private transient Float[][] delayStatsForAllRuns ;
     public String simulationName;
     public String timeStamp;
     public String RunType;
     private Number[] valuesOfVariableParam;

    public Result(int numberOfPoints, int numberOfRuns, Number[] valuesOfVariableParam, String RunType,String simulationName , String timeStamp){
         costStats = new Float[numberOfPoints];
         delayStats = new Float[numberOfPoints];
         costStatsForAllRuns = new Float[numberOfPoints][numberOfRuns];
         delayStatsForAllRuns = new Float[numberOfPoints][numberOfRuns];
         this.simulationName = simulationName;
         this.timeStamp = timeStamp;
         this.valuesOfVariableParam = valuesOfVariableParam;
         this.RunType = RunType;
    }


    public Float[] gatherStats(List<Client> clients ) {
        int totalCost = 0;
        float counter = 0;
        float totalDelay = 0f;
        for (Client client:clients) {
            for (int reqID:client.getServedRequestsCost().keySet()){
                float sendTime = client.getSentRequestsTime().get(reqID);
                float servedTime = client.getServedRequestsTime().get(reqID);
                totalDelay +=servedTime-sendTime;
                int cost = client.getServedRequestsCost().get(reqID);
                totalCost+=cost;
                counter++;
            }
        }
        Float[] tempRes = new Float[2];
        tempRes[0] = totalCost/counter;
        tempRes[1] = totalDelay/counter;
        return tempRes;
//        costStatsForAllRuns[i][j] = totalCost/counter;
//        delayStatsForAllRuns[i][j]= totalDelay/counter;
    }

    public void putStatsInTables(int i , int j , Float[] res ) {
        costStatsForAllRuns[i][j] = res[0];
        delayStatsForAllRuns[i][j] = res[1];
    }


    public void calcAverageOnAllRuns(int i) {
        float costSum = 0 , delaySum = 0;
        int numberOfRuns = costStatsForAllRuns[0].length;
        for (int j = 0; j < numberOfRuns; j++) {
            costSum+=costStatsForAllRuns[i][j];
            delaySum+=delayStatsForAllRuns[i][j];
        }
        costStats[i] = costSum/numberOfRuns;
        delayStats[i] = delaySum/numberOfRuns;

    }

    public void calcAverageOnAllRunsOnAllPoints() {
        for (int i = 0; i < costStats.length; i++) {
            calcAverageOnAllRuns(i);
        }
        if (RunType.equals("D")| RunType.equals("P")) {
            costStats = new Float[valuesOfVariableParam.length];
            for (int i = 0; i < valuesOfVariableParam.length; i++) {
                costStats[i] = valuesOfVariableParam[i].floatValue();
            }
        }
    }


    public Float[] getCostStats() {
        return costStats;
    }

    public Float[] getDelayStats() {
        return delayStats;
    }
}
