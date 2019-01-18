package entities.Statistics;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
//
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Chart extends Application {
    public static ArrayList<Number[]> xValuesSeries;
    public static ArrayList<Number[]> yValuesSeries;
    public static String[] lineChartLabels;
    public static ArrayList<String> seriesName;
    public static String path;
    /***
     *              [LineChartTitle, xAxisLabel, yAxisLabel ]
     * */
    /***
     *              [seriesName]
     * */

    public static void addSeries(String seriesName, Number[] xValues, Number[] yValues) {
        Chart.xValuesSeries.add(xValues);
        Chart.yValuesSeries.add(yValues);
        Chart.seriesName.add(seriesName);
    }

    public static void initiateChart(String pathName, String RunType) {
        String XAxisName="";
        switch (RunType){
            case "D":
                XAxisName = "Honeybee random factor";
                break;
            case "P":
                    XAxisName = "Update step(ms)";
                    break;
            case "Regular":
                default:
                XAxisName = "Communication Cost" ;
        }
        String[] chartLabels = {"Communication cost vs. load balancing",XAxisName,"Waiting Time(ms)"};
        Chart.lineChartLabels = chartLabels;
        Chart.xValuesSeries = new ArrayList<>();
        Chart.yValuesSeries = new ArrayList<>();
        Chart.seriesName = new ArrayList<>();
        Chart.path = pathName;
    }




    @Override public void start(Stage stage) {
        stage.setTitle("ICDNSim");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
//        final NumberAxis yAxis = new NumberAxis(300,440,10);
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        xAxis.setLabel(lineChartLabels[1]);
        yAxis.setLabel(lineChartLabels[2]);
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle(lineChartLabels[0]);
        //defining a series
        lineChart.setAnimated(true);

        for (int i = 0; i < seriesName.size(); i++) {
            XYChart.Series series = new XYChart.Series();
            series.setName(seriesName.get(i));
            Number[] xValues = xValuesSeries.get(i);
            Number[] yValues = yValuesSeries.get(i);
            for (int j = 0; j < xValues.length; j++) {
                series.getData().add(new XYChart.Data<>(xValues[j], yValues[j]));
            }
            lineChart.getData().add(series);
        }
        Scene scene  = new Scene(lineChart,1024,1024);
        saveAsPng(scene);
        stage.setScene(scene);
        stage.show();
    }


    public void saveAsPng(Scene scene) {
        WritableImage image = scene.snapshot(null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

