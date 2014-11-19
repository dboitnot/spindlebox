package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import spindlebox.MonitorSession.MonitorSessionFactory;
import spindlebox.settings.AccountSettings;
import spindlebox.ui.StateMonitor;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static spindlebox.ui.StateMonitor.State.DISCONNECTED;
import static spindlebox.util.Logging.DEBUG;
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

    private final AtomicReference<Optional<Runnable>> closeHandle = new AtomicReference<>(Optional.empty());

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
        closeHandle.get().ifPresent(Runnable::run);
        pool.shutdownNow();
        try {
            pool.awaitTermination(30, SECONDS);
        } catch (InterruptedException ignored) {}
    }

    private void openSession() {
        try {
            // Start a monitoring session
            closeHandle.set(Optional.empty());
            monitorSessionFactory.create(accountSettings).start(closeHandle);
            DEBUG("Monitor session ended without exception.");
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
