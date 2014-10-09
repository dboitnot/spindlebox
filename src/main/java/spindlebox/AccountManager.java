package spindlebox;

import spindlebox.settings.AccountSettings;

/**
 * spindlebox: Account
 * Created by dboitnot on 10/7/14.
 */
public interface AccountManager {
    public static interface AccountManagerFactory {
        AccountManager create(AccountSettings settings);
    }

    public void start();
    public void shutdown();
}
