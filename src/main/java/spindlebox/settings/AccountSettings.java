package spindlebox.settings;

import java.util.Optional;

/**
 * spindlebox: AccountSettings
 * Created by dboitnot on 10/7/14.
 */
public interface AccountSettings {
    public Optional<String> getTitle();
    public Optional<String> getHost();
    public Optional<String> getUsername();
    public Optional<String> getInbox();
    public Optional<Long> getInboxPollingInterval();
    public Optional<Long> getBoxHandlerPollingInterval();

    public default Optional<String> getStoredPassword() {
        return Optional.empty();
    }

    public default String getLabel() {
        return getTitle().orElse(String.format("%s@%s", getUsername().orElse("unknown"), getHost().orElse("unknown")));
    }
}
