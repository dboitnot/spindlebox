package spindlebox;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import spindlebox.settings.SettingsSource;
import spindlebox.ui.StateMonitor;
import spindlebox.util.Shared;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static spindlebox.ui.StateMonitor.State.SHUTTING_DOWN;
import static spindlebox.util.Logging.INFO;
import static spindlebox.util.Logging.WARN;

@Singleton
public class AppControlImpl implements AppControl {
    private final SettingsSource settingsSource;
    private final AccountManager.AccountManagerFactory accountManagerFactory;
    private final StateMonitor stateMonitor;
    private final ScheduledExecutorService sharedPool;

    private final List<AccountManager> accountManagers = new ArrayList<>();

    private boolean started = false;
    private boolean shuttingDown = false;

    @Inject
    public AppControlImpl(
            SettingsSource settingsSource,
            AccountManager.AccountManagerFactory accountManagerFactory,
            StateMonitor stateMonitor,
            @Shared ScheduledExecutorService sharedPool)
    {
        this.settingsSource = settingsSource;
        this.accountManagerFactory = accountManagerFactory;
        this.stateMonitor = stateMonitor;
        this.sharedPool = sharedPool;
    }

    @Override
    public synchronized void startup() {
        if (started) {
            WARN("startup() called more than once, ignoring");
            return;
        }

        started = true;

        settingsSource.getAccounts().forEach(accountSettings -> {
            AccountManager m = accountManagerFactory.create(accountSettings);
            accountManagers.add(m);
            m.start();
        });
    }

    @Override
    public synchronized void shutdown(boolean userConfirm) {
        if (shuttingDown) {
            WARN("shutdown() called more than once, ignoring");
            return;
        }

        shuttingDown = true;

        INFO("Spindlebox shutting down...");
        sharedPool.shutdownNow();
        stateMonitor.stateChanged(SHUTTING_DOWN);
        accountManagers.forEach(AccountManager::shutdown);
        System.exit(0);
    }
}
