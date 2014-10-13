package spindlebox.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.Level.*;

/**
 * spindlebox: Logging
 * Created by dboitnot on 10/11/14.
 */
public class Logging {
    private static StackTraceElement getCaller() {
        StackTraceElement[] es = Thread.currentThread().getStackTrace();
        for (int i = 3; i < es.length; i++) {
            StackTraceElement e = es[i];
            if (!e.getClassName().equals(Logging.class.getName()))
                return e;
        }
        throw new RuntimeException("called out of thread");
    }

    private static Logger getLogger() {
        StackTraceElement caller = getCaller();
        String cls = caller.getClassName();
        int dlr = cls.indexOf('$');
        if (dlr > 0)
            cls = cls.substring(0, dlr);
        return LogManager.getLogger(cls);
    }

    public static void log(Level lvl, String fmt, Object... args) {
        getLogger().log(lvl, fmt, args);
    }

    public static void log(Level lvl, String msg, Throwable t) {
        getLogger().log(lvl, msg, t);
    }

    public static void DEBUG(String fmt, Object... args) {
        log(DEBUG, fmt, args);
    }

    public static void INFO(String fmt, Object... args) {
        log(INFO, fmt, args);
    }

    public static void WARN(String fmt, Object... args) {
        log(WARN, fmt, args);
    }

    public static void WARN(String msg, Throwable t) {
        log(WARN, msg, t);
    }
}
