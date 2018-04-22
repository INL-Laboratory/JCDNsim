package entities.utilities;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import java.util.HashMap;


public class Chart extends Application {
    public static float[] xValues;
    public static float[] yValues;

    @Override public void start(Stage stage) {
        stage.setTitle("Line Chart");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("value");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Frequency in 10000 runs");
        //defining a series



        XYChart.Series series = new XYChart.Series();
        series.setName("# of adjacent nodes of an arbitrary node");
        for (int i = 0;i<xValues.length;i++) {
            series.getData().add(new XYChart.Data<>(xValues[i], yValues[i]));
        }
        lineChart.getData().add(series);

//            XYChart.Series series2 = new XYChart.Series();
//            series2.setName("Size of largest connected components");
//            for (int key:largests.keySet()) {
//                series2.getData().add(new XYChart.Data<>(key, largests.get(key)));
//            }
//            lineChart.getData().add(series2);

        Scene scene  = new Scene(lineChart,1024,960);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

