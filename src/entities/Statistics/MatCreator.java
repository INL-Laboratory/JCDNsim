/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Statistics;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

import java.io.*;
import java.util.*;

public class MatCreator {
    static double a = 10;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("/Users/saeedhd/IdeaProjects/ICDNSim/matFile/TradeOff");
        String[] list = file.list();
        for (String f:list) {
            ObjectInputStream objectInputStream = null;
            FileInputStream fileInputStream = null;
            try {
                System.out.println(f);
                fileInputStream = new FileInputStream("/Users/saeedhd/IdeaProjects/ICDNSim/matFile/TradeOff/"+f);
                objectInputStream = new ObjectInputStream(fileInputStream);
                Result result = (Result)objectInputStream.readObject();
                System.out.println(result.simulationName);



                String RunType = result.simulationName.substring(0,result.simulationName.indexOf("-"));
                String XAxisName="";
                switch (RunType){
                    case "D":
                        XAxisName = "Random Search Proportion";
                        break;
                    case "P":
                        XAxisName = "Update Step (ms)";
                        break;
                    case "Regular":
                    default:
                        XAxisName = "Communication Cost" ;
                }
                String legend= "";
                if (result.simulationName.contains("HONEYBEE")){
                    legend = "Honey Bee, Random fraction = 0.05, Update Step = 270 ms";
                }else if (result.simulationName.contains("ideal")){
                    legend="Ideal";
                }
                else if (result.simulationName.contains("WMC-periodic")){
                    legend = "Periodic, Update Step = 270 ms";
                }else if (result.simulationName.contains("piggyBack")){
                    legend = "PiggyBack";
                }



                Number[] xValues = result.getCostStats();

                Number[] yValues = result.getDelayStats();

                MLDouble mlsx = new MLDouble("x", new int[] {xValues.length,2});
                MLChar mlChar = new MLChar("xLabel",XAxisName);
                MLChar mlChar2 = new MLChar("yLabel","Average Waiting Time(ms)");
//                MLChar mlChar4 = new MLChar("zLabel","cache-size");
                MLChar mlChar3 = new MLChar("legend",legend);
                int c = 0;
                for (int i = 0; i < xValues.length; i++) {
                    mlsx.set(xValues[i].doubleValue(),c);
                    c++;
                }
                for (int i = 0; i < yValues.length; i++) {
                    mlsx.set(yValues[i].doubleValue(),c);
                    c++;
                }

//                int s = result.simulationName.indexOf("cacheSize");
//                a = Double.parseDouble(result.simulationName.substring(s+10,s+12));

//                for (int i = 0; i < yValues.length; i++) {
//                    mlsx.set(a,c);
//                    c++;
//                }
//                String z = result.simulationName.substring(result.simulationName.lastIndexOf("-")+1);
//                for (int i = 0; i < yValues.length; i++) {
//                    mlsx.set(Double.parseDouble(z),c);
//                    c++;
//                }



                ArrayList<MLArray> list3 = new ArrayList<>();
                list3.add(mlsx);
                list3.add(mlChar);
                list3.add(mlChar2);
                list3.add(mlChar3);
//                list3.add(mlChar4);
                MatFileWriter writer = new MatFileWriter("salam.mat", list3);
                writer.write("matFile/TradeOff/"+result.simulationName+".mat", list3);



            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if(fileInputStream!=null) {
                        fileInputStream.close();
                    }if (objectInputStream!=null) {
                        objectInputStream.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
}


