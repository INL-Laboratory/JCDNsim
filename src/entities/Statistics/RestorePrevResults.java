package entities.Statistics;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestorePrevResults {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("/Users/hd/IdeaProjects/ICDNSim/restore");
        String[] list = file.list();
        String photoPathName = "/Users/hd/IdeaProjects/ICDNSim/restore/photo.png";
        Chart.initiateChart(photoPathName);
        Map<Number,Number> res = new HashMap<>();
        for (String f:list) {
            ObjectInputStream objectInputStream = null;
            FileInputStream fileInputStream = null;
            try {
                System.out.println(f);
                fileInputStream = new FileInputStream("/Users/hd/IdeaProjects/ICDNSim/restore/"+f);
                objectInputStream = new ObjectInputStream(fileInputStream);
                Result result = (Result)objectInputStream.readObject();
                System.out.println(result.simulationName);
//                result.getDelayStats()[9]/=10;

//                result.getDelayStats()[10]/=5;
//                res.put(Float.parseFloat(result.simulationName.substring(result.simulationName.lastIndexOf("-")+1)),result.getDelayStats()[0]);
//                res.put(Float.parseFloat(result.simulationName.substring(result.simulationName.indexOf("-")+1,result.simulationName.indexOf("-",result.simulationName.indexOf("-")+1))),result.getDelayStats()[0]);
                Number[] xValues = result.getCostStats();
                Number[] yValues = result.getDelayStats();
                int c = 0;
                for (Number i: res.keySet()) {
                    xValues[c]= i;
                    yValues[c]= res.get(i);
                    c++;
                }
//        Chart.addSeries("HoneyBees",xValues,yValues);
                Chart.addSeries(result.simulationName,xValues,yValues);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    fileInputStream.close();
                    objectInputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
//        Integer[] xValues = new Integer[res.size()];
//        Float[] yValues = new Float[res.size()];
//        int c = 0;
//        for (int i:
//             res.keySet()) {
//            xValues[c]= i;
//            yValues[c]= res.get(i);
//            c++;
//        }
//        Chart.addSeries("Periodics",xValues,yValues);
//        Number[] xValues = new Number[res.size()];
//        Number[] yValues = new Number[res.size()];
//        int c = 0;
//        for (Number i: res.keySet()) {
//            xValues[c]= i;
//            yValues[c]= res.get(i);
//            c++;
//        }
//        Chart.addSeries("HoneyBees",xValues,yValues);
//        Chart.addSeries("HoneyBees",xValues,yValues);
        Chart.main(args);

    }
}
