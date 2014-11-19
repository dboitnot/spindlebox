package spindlebox;

import spindlebox.settings.AccountSettings;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * spindlebox: MonitorSession
 * Created by dboitnot on 10/10/14.
 */
public interface MonitorSession {
    public static interface MonitorSessionFactory {
        MonitorSession create(AccountSettings settings);
    }

    public static class FatalSessionException extends Exception {
        public FatalSessionException(String message) {
            super(message);
        }

        public FatalSessionException(String message, Throwable cause) {
            super(message, cause);
        }

        public FatalSessionException(Throwable cause) {
            super(cause);
        }
    }

    public void start(AtomicReference<Optional<Runnable>> closeHandle) throws FatalSessionException;
}
