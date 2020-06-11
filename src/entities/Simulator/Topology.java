/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

public class Topology {
    public boolean[][] adjMat;
    public int[][] weight;
    public float costNormalizationFactor;
    public double averageWeight;

    public Topology(boolean[][] adjMat, int[][] weight, float costNormalizationFactor, float averageWeight) {
        this.adjMat = adjMat;
        this.weight = weight;
        this.averageWeight=averageWeight;
        this.costNormalizationFactor = costNormalizationFactor;
    }
}
