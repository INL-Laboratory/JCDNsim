# Readme


# Where to go?
The main file of the simulator is /entities/Simulator/RunSimulator.java.
In this file, the main() function is responsible for running the simulator. The section in which running commands should be modified is separated with //Run section and //End of run section.

# What to modify?

Firstly, you should specify the value of the variable parameter you want to analyze:

    Number[] points1 = {a,b,...,z};
Then, you should specify the run type:
        
    runType = "The desired run type";

After specifying the runtime, you should make the bundle of the constant parameters for all algorithms you want to analyze:

    Map<String,Number> bundle1 = new HashMap<>();
    bundle1.put(AlgParamsList.The_First_Algorithm_Parameter.toString(),constant value);
    bundle1.put(AlgParamsList.The_Second_Algorithm_Parameter.toString(),constant value);

Also, you should specify the run parameters and pass the constant parameter you built in the previous step:

    RunningParameters runParams= new RunningParameters(runType,"Redirecting Algorithm", "Update Algorithm",bundle1,AlgParamsList.The_Variable_Parameter.toString(), points1, configuration,path);
        
Finally, you should order the run, while passing the adjacency matrix of the desired topology :

    runSimulator.run(runParams, pool ,latticeTopology);


# How to fill the run section?

You can use the following guideline:

* Set Redirecting Algorithm Parameter Names: 

           "WMC":
                Parameter Name = "WMC_ALPHA";
           "PSS":
                Parameter Name = "PSS_PROBABILITY";
           "MCS":
                Parameter Name = "MCS_DELTA";
           "CostBased":
                Parameter Name = "Radius";
           "HONEYBEE":
                Parameter Name = "WMC_ALPHA";
		Parameter Name = "HONEY_BEE_SEARCH_PROBABILITY";

* Set Update Algorithm Parameters' Names: 
           
	         "piggyBack":
        		    No parameter
           "periodic":
                Parameter = "periodicStep";
           "piggyGroupedPeriodic":
                Parameter = "periodicStep";
           "ideal":
		            No parameter

* Run types:
	Specifying the run type is only useful for the sake of correct names on the final chart. The right choice of algorithms and parameters is up to you based on the above guidelines.

	        "P" : In order to find periodic's update step optimum value
	        "Regular" : In order to analyze trade-off between Honey bee
	        "D" : In order to find the optimum value of Honeybee random search factor


# Example

        Number[] points1 = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
        runType = "Regular";
        Map<String,Number> bundle1 = new HashMap<>();
        bundle1.put(AlgParamsList.periodicStep.toString(),400);
        bundle1.put(AlgParamsList.HONEY_BEE_SEARCH_PROBABILITY.toString(),0);
        RunningParameters runParams= new RunningParameters(runType,"WMC", "periodic",bundle1,AlgParamsList.WMC_ALPHA.toString(), points1, configuration,path);
        runSimulator.run(runParams,pool, latticeTopology);



# Contact

Should you have any questions, please contact me at: saeedhd@ce.sharif.edu or 2nd.silverist@gmail.com
