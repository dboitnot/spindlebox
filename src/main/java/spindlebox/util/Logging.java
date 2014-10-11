package spindlebox.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import static org.apache.logging.log4j.Level.*;

/**
 * spindlebox: Logging
 * Created by dboitnot on 10/11/14.
 */
public interface Logging {
    default String getLoggingPrefix() {
        return null;
    }

    default void _log(Level lvl, String fmt, Object... args) {
        LogManager.getLogger(getClass()).log(lvl, _buildLoggingPrefix(getLoggingPrefix()) + fmt, args);
    }

    default void _log(Level lvl, String msg, Throwable t) {
        LogManager.getLogger(getClass()).log(lvl, _buildLoggingPrefix(getLoggingPrefix()) + msg, t);
    }

    default void DEBUG(String fmt, Object... args) {
        _log(DEBUG, fmt, args);
    }

    default void INFO(String fmt, Object... args) {
        _log(INFO, fmt, args);
    }

    default void WARN(String fmt, Object... args) {
        _log(WARN, fmt, args);
    }

    default void WARN(String msg, Throwable t) {
        _log(WARN, msg, t);
    }

    static String _buildLoggingPrefix(String prefix) {
        return prefix == null ? "" : "<" + prefix + "> ";
    }
}
