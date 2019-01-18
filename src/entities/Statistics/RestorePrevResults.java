package entities.Statistics;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestorePrevResults {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("/Users/hd/IdeaProjects/ICDNSim/restore");
        String[] list = file.list();
        boolean firstTime = true;
        String photoPathName = "/Users/hd/IdeaProjects/ICDNSim/restore/photo.png";
        Map<HoneyBeeStatPair,Map<Number,Number>> res = new HashMap<>();
        Map<Number,Number> resReg = new HashMap<>();
        for (String f:list) {
            ObjectInputStream objectInputStream = null;
            FileInputStream fileInputStream = null;
            try {
                System.out.println(f);
                fileInputStream = new FileInputStream("/Users/hd/IdeaProjects/ICDNSim/restore/"+f);
                objectInputStream = new ObjectInputStream(fileInputStream);
                Result result = (Result)objectInputStream.readObject();
                System.out.println(result.simulationName);





                if (firstTime){
                    firstTime = false;
                    Chart.initiateChart(photoPathName, result.simulationName.substring(0,result.simulationName.indexOf("-")));
                }
                Number[] xValues = result.getCostStats();
//                Number[] xValues =  {0,0.02,0.04,0.06,0.08,0.1,0.12,0.14,0.16,0.18,0.2,0.22,0.24,0.26,0.28,0.30};

                Number[] yValues = result.getDelayStats();
                int c = 0;
//                for (Number i: resReg.keySet()) {
//                    xValues[c]= i;
//                    yValues[c]= resReg.get(i);
//                    c++;
//                }
                Chart.addSeries(result.simulationName,xValues,yValues);

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


//        for (HoneyBeeStatPair pair:res.keySet()) {
//            Number[] xValues = new Number[res.get(pair).size()];
//            Number[] yValues = new Number[res.get(pair).size()];
//            int c = 0;
//            for (Number i : res.get(pair).keySet()) {
//                xValues[c] = i;
//                yValues[c] = res.get(pair).get(i);
//                c++;
//            }
//            Chart.addSeries(pair.alpha+"-"+pair.updatePeriod,xValues,yValues);
//        }



//        Chart.addSeries("HoneyBees",xValues,yValues);
        Chart.main(args);

    }
}


class HoneyBeeStatPair{
    float alpha;
    int updatePeriod;

    public HoneyBeeStatPair(float alpha, int updatePeriod) {
        this.alpha = alpha;
        this.updatePeriod = updatePeriod;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoneyBeeStatPair that = (HoneyBeeStatPair) o;
        return Float.compare(that.alpha, alpha) == 0 &&
                updatePeriod == that.updatePeriod;
    }

    @Override
    public int hashCode() {

        return Objects.hash(alpha, updatePeriod);
    }
}