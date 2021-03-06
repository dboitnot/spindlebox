package spindlebox.settings;

import java.time.Duration;
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
    public Optional<Duration> getInboxPollingInterval();
    public Optional<Duration> getBoxHandlerPollingInterval();
    public Optional<Duration> getTimeout();
    public Optional<Boolean> isDebug();

    public default Optional<String> getStoredPassword() {
        return Optional.empty();
    }

    public default String getLabel() {
        return getTitle().orElse(String.format("%s@%s", getUsername().orElse("unknown"), getHost().orElse("unknown")));
    }
}
