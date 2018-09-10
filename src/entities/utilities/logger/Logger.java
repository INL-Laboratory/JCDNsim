package entities.utilities.logger;
import entities.logical.DefaultValues;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
    static Calendar now = Calendar.getInstance();
    static SimpleDateFormat dtf = new SimpleDateFormat("d MMM uuuu HH:mm:ss");


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
        return dtf.format(now.getTime());
    }

    public static void main(String[] args) {
    }

}
