package spindlebox.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * spindlebox: Logging
 * Created by dboitnot on 10/11/14.
 */
public interface Logging {
    default Logger _logger() {
        return LogManager.getLogger(getClass());
    }

    default void DEBUG(String fmt, Object... args) {
        _logger().debug(fmt, args);
    }

    default void INFO(String fmt, Object... args) {
        _logger().info(fmt, args);
    }

    default void WARN(String fmt, Object... args) {
        _logger().warn(fmt, args);
    }

    default void WARN(String msg, Throwable t) {
        _logger().warn(msg, t);
    }
}
