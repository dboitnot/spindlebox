package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import spindlebox.util.Logging;
import spindlebox.passwords.PasswordService;
import spindlebox.settings.AccountSettings;

/**
 * spindlebox: AccountManagerImpl
 * Created by dboitnot on 10/9/14.
 */
public class AccountManagerImpl implements AccountManager, Logging {
    private final AccountSettings accountSettings;

    private final PasswordService passwordService;

    @Inject
    public AccountManagerImpl(@Assisted AccountSettings accountSettings, PasswordService passwordService) {
        this.accountSettings = accountSettings;
        this.passwordService = passwordService;
    }

    @Override
    public void start() {
        INFO("Starting: {}", accountSettings.getLabel());
    }

    @Override
    public void shutdown() {

    }
}
