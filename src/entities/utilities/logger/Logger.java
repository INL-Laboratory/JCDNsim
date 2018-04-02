package entities.utilities.logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static LocalDateTime now = LocalDateTime.now();
    public static void print(String toPrint, float time){
        System.out.println(toPrint);
    }
    public static void printWithClock(String toPrint, float time){

        print(getRealTime()+ " " + toPrint, time);
    }

    public static String getRealTime(){
        return dtf.format(now);
    }

    public static void main(String[] args) {
    }

}
