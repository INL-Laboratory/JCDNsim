package entities.Statistics;

import java.io.*;

public class RestorePrevResults {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("/Users/hd/IdeaProjects/ICDNSim/restore");
        String[] list = file.list();
        String photoPathName = "/Users/hd/IdeaProjects/ICDNSim/restore/photo.png";
        Chart.initiateChart(photoPathName);
        for (String f:list) {
            ObjectInputStream objectInputStream = null;
            FileInputStream fileInputStream = null;
            try {
                System.out.println(f);
                fileInputStream = new FileInputStream("/Users/hd/IdeaProjects/ICDNSim/restore/"+f);
                objectInputStream = new ObjectInputStream(fileInputStream);
                Result result = (Result)objectInputStream.readObject();
                System.out.println(result.simulationName);
                Chart.addSeries(result.simulationName,result.getCostStats(),result.getDelayStats());
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

        Chart.main(args);

    }
}
