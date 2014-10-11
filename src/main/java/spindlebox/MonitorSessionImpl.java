package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import spindlebox.passwords.PasswordService;
import spindlebox.settings.AccountSettings;
import spindlebox.util.Logging;

/**
 * spindlebox: MonitorSessionImpl
 * Created by dboitnot on 10/10/14.
 */
public class MonitorSessionImpl implements MonitorSession, Logging {
    private final AccountSettings settings;
    private final PasswordService passwordService;

    @Inject
    public MonitorSessionImpl(@Assisted AccountSettings settings, PasswordService passwordService) {
        this.settings = settings;
        this.passwordService = passwordService;
    }

    @Override
    public void start() {
        DEBUG("Starting session: {}", settings.getLabel());
    }
}
