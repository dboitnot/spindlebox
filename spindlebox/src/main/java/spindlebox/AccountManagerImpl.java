package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import spindlebox.MonitorSession.MonitorSessionFactory;
import spindlebox.settings.AccountSettings;
import spindlebox.ui.StateMonitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;
import static spindlebox.ui.StateMonitor.State.DISCONNECTED;
import static spindlebox.util.Logging.INFO;
import static spindlebox.util.Logging.WARN;

/**
 * spindlebox: AccountManagerImpl
 * Created by dboitnot on 10/9/14.
 */
public class AccountManagerImpl implements AccountManager {
    private static final long RETRY_DELAY = 1;
    private static final TimeUnit RETRY_DELAY_UNIT = MINUTES;

    private final AccountSettings accountSettings;
    private final MonitorSessionFactory monitorSessionFactory;
    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
    private final StateMonitor stateMonitor;

    @Inject
    public AccountManagerImpl(@Assisted AccountSettings accountSettings,
                              MonitorSessionFactory monitorSessionFactory,
                              StateMonitor stateMonitor)
    {
        this.accountSettings = accountSettings;
        this.monitorSessionFactory = monitorSessionFactory;
        this.stateMonitor = stateMonitor;
    }

    @Override
    public void start() {
        INFO("Starting: {}", accountSettings.getLabel());
        stateMonitor.stateChanged(DISCONNECTED);
        pool.scheduleWithFixedDelay(this::openSession, 0, RETRY_DELAY, RETRY_DELAY_UNIT);
    }

    @Override
    public void shutdown() {
        INFO("Stopping account manager for {}", accountSettings.getLabel());
        pool.shutdownNow();
    }

    private void openSession() {
        try {
            // Start a monitoring session
            monitorSessionFactory.create(accountSettings).start();
            WARN("Monitor session ended without exception. This isn't supposed to happen.");
        } catch (MonitorSession.FatalSessionException ex) {
            WARN("Fatal session exception. Shutting down account monitor.", ex);
            shutdown();
        } catch (Exception ex) {
            // Catch any exception so that the pool does not stop retrying.
            WARN("Monitor session ended unexpectedly", ex);
            INFO("Monitor session will restart in {} {}", RETRY_DELAY, RETRY_DELAY_UNIT);
        }
    }
}
