import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZonedDateTime;

public class DateTimeHandler {
    public static String getDateTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String result = dtf.format(now);
        return result;
    }

    public static String getDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String result = dtf.format(now);
        return result;
    }

    public static String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String result = dtf.format(now);
        return result;
    }

    public static long msGetTime(){
        Date date = new Date();
        long timeMilli = date.getTime();
        return timeMilli;
    }

    public static long msDifference(long x, long y){
        return x-y;
    }
}

/* References: 

DateTime: https://www.javatpoint.com/java-get-current-date
Millisecond time: https://beginnersbook.com/2014/01/how-to-get-time-in-milliseconds-in-java/

*/