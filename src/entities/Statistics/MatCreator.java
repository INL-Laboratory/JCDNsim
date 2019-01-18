package entities.Statistics;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

import java.io.*;
import java.util.*;

public class MatCreator {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("/Users/hd/IdeaProjects/ICDNSim/matFile");
        String[] list = file.list();
        for (String f:list) {
            ObjectInputStream objectInputStream = null;
            FileInputStream fileInputStream = null;
            try {
                System.out.println(f);
                fileInputStream = new FileInputStream("/Users/hd/IdeaProjects/ICDNSim/matFile/"+f);
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
                    legend = "Honey Bee, Random fraction = 0.06, Update Step = 500 ";
                }else if (result.simulationName.contains("ideal")){
                    legend="Ideal";
                }
                else if (result.simulationName.contains("periodic-periodicStep-460")){
                    legend = "Periodic, Update Step = 460";
                }else if (result.simulationName.contains("periodic-periodicStep-400")){
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
                writer.write("matFile/"+result.simulationName+".mat", list3);



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


