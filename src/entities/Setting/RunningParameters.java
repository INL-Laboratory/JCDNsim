/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Setting;


import java.util.Map;
import java.util.concurrent.ExecutorService;

public class RunningParameters {

    public final String RunType;
    public final String algorithm;
    public final String updateType;
    public final Map<String,Number> fixedParamsBundle;
    public final String variableParam;
    public final Number[] valuesOfVariableParam;
    public final Configuration configuration;
    public final String path;

    public RunningParameters(String runType, String algorithm, String updateType, Map<String, Number> fixedParamsBundle, String variableParam, Number[] valuesOfVariableParam, Configuration configuration, String path) {
        RunType = runType;
        this.algorithm = algorithm;
        this.updateType = updateType;
        this.fixedParamsBundle = fixedParamsBundle;
        this.variableParam = variableParam;
        this.valuesOfVariableParam = valuesOfVariableParam;
        this.configuration = configuration;
        this.path = path;
    }
}
