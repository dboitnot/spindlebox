package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import spindlebox.passwords.PasswordService;
import spindlebox.settings.AccountSettings;

import java.util.logging.Logger;

/**
 * spindlebox: AccountManagerImpl
 * Created by dboitnot on 10/9/14.
 */
public class AccountManagerImpl implements AccountManager {
    private final AccountSettings accountSettings;

    private final PasswordService passwordService;
    private final Logger log;

    @Inject
    public AccountManagerImpl(@Assisted AccountSettings accountSettings, PasswordService passwordService, Logger log) {
        this.accountSettings = accountSettings;
        this.passwordService = passwordService;
        this.log = log;
    }

    @Override
    public void start() {
        log.info("Starting: " + accountSettings.getTitle().orElse("Untitled Account"));
    }

    @Override
    public void shutdown() {

    }
}
