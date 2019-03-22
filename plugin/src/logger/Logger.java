package logger;

import constants.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void log(String msg) {
        log(msg, "Undefined");
    }

    public static void log(String msg, String source) {
        if(Constants.DEBUG_MODE) {
            String logString = getCurrentLogTime() + "\t" + msg + " | " + source;
            System.out.println(logString);
        }
    }

    private static String getCurrentLogTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        return dateFormat.format(now);
    }
}
