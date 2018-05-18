package entities.utilities.logger;
import entities.logical.DefaultValues;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static LocalDateTime now = LocalDateTime.now();
    public static PrintWriter printWriter;


    public Logger() throws IOException {
    }


    //    private static OutputStream ;
    public static void print(String toPrint, float time){
        if (!DefaultValues.LOGGER_ON) return;
//        printWriter.flush();
//        printWriter.println(time + " " + toPrint);
        System.out.println(time + " " + toPrint);
    }
    public static void printWithoutTime(String toPrint){
        if (!DefaultValues.LOGGER_ON) return;
//        printWriter.println( toPrint);
        System.out.println(toPrint);

    }


    public static void ePrint(String toPrint, float time){
        if (!DefaultValues.LOGGER_ON) return;
//        print(toPrint,time);
    }

    public static void printWithClock(String toPrint, float time){
        if (!DefaultValues.LOGGER_ON) return;
        print(getRealTime()+ " " + toPrint, time);
    }

    public static String getRealTime(){
        return dtf.format(now);
    }

    public static void main(String[] args) {
    }

}
