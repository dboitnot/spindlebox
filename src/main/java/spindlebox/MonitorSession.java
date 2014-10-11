package spindlebox;

import spindlebox.settings.AccountSettings;

/**
 * spindlebox: MonitorSession
 * Created by dboitnot on 10/10/14.
 */
public interface MonitorSession {
    public static interface MonitorSessionFactory {
        MonitorSession create(AccountSettings settings);
    }

    public void start();
}
