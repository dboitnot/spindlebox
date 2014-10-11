package spindlebox;

import spindlebox.settings.AccountSettings;
import spindlebox.util.Logging;

/**
 * spindlebox: MonitorSessionImpl
 * Created by dboitnot on 10/10/14.
 */
public class MonitorSessionImpl implements MonitorSession, Logging {
    @Override
    public void monitor(AccountSettings settings) {
        DEBUG("Starting session: " + settings.getLabel());
    }
}
