package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import spindlebox.MonitorSession.MonitorSessionFactory;
import spindlebox.settings.AccountSettings;
import spindlebox.util.Logging;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * spindlebox: AccountManagerImpl
 * Created by dboitnot on 10/9/14.
 */
public class AccountManagerImpl implements AccountManager, Logging {
    private final AccountSettings accountSettings;

    private final MonitorSessionFactory monitorSessionFactory;

    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    @Inject
    public AccountManagerImpl(@Assisted AccountSettings accountSettings, MonitorSessionFactory monitorSessionFactory) {
        this.accountSettings = accountSettings;
        this.monitorSessionFactory = monitorSessionFactory;
    }

    @Override
    public void start() {
        INFO("Starting: {}", accountSettings.getLabel());
        pool.scheduleWithFixedDelay(this::openSession, 0, 1, TimeUnit.MINUTES);
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
        }
    }
}
